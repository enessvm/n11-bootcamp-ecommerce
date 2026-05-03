import { useEffect, useMemo, useRef, useState } from 'react';
import { Link, useNavigate, Navigate} from 'react-router-dom';
import {
  CheckCircle2,
  ImageOff,
  Loader2,
  MapPin,
  Plus,
  ShoppingCart,
  Tag,
  X,
} from 'lucide-react';
import { useQueryClient } from '@tanstack/react-query';
import { useAuth } from '@/auth/useAuth';
import { usePageTitle } from '@/hooks/usePageTitle';
import { useCart } from '@/hooks/useCart';
import { useAddresses, useCreateAddress } from '@/hooks/useAddresses';
import { useValidatePromotion } from '@/hooks/usePromotionValidation';
import { useCreateOrder } from '@/hooks/useOrders';
import { useOrder } from '@/hooks/useOrder';
import { clearCart as clearCartApi } from '@/api/cart';
import { isTerminalSagaState } from '@/lib/sagaState';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Skeleton } from '@/components/ui/skeleton';
import { AddressCard } from '@/components/address/AddressCard';
import { AddressForm } from '@/components/address/AddressForm';
import { PriceTag } from '@/components/product/PriceTag';
import { extractErrorMessage } from '@/api/client';
import { formatMoney } from '@/lib/format';
import {
  computeCartDiscount,
  describePromotion,
  describePromotionFailure,
} from '@/lib/discount';
import { toast } from 'sonner';

export function CheckoutPage() {
  const { isAuthenticated, isLoading: authLoading } = useAuth();
  const navigate = useNavigate();

  if (!authLoading && !isAuthenticated) {
    return <Navigate replace to="/login?returnTo=/checkout" navigate={navigate} />;
  }

  return <CheckoutContent />;
}

function CheckoutContent() {
  usePageTitle('Checkout');
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { data: cart, isLoading: cartLoading } = useCart();
  const { data: addresses, isLoading: addressesLoading } = useAddresses();

  const items = cart?.items ?? [];
  const subtotalAmount = Number(cart?.subtotal?.amount ?? 0);
  const currency = cart?.subtotal?.currency ?? 'TRY';

  // Address selection
  const [shippingId, setShippingId] = useState(null);
  const [billingSameAsShipping, setBillingSameAsShipping] = useState(true);
  const [billingId, setBillingId] = useState(null);
  const [showAddForm, setShowAddForm] = useState(false);
  const createAddress = useCreateAddress();

  // Coupon
  const [couponInput, setCouponInput] = useState('');
  const [appliedPromotion, setAppliedPromotion] = useState(null);
  const validate = useValidatePromotion();

  // Order placement + payment popup tracking
  const placeOrder = useCreateOrder();
  const [pendingOrderId, setPendingOrderId] = useState(null);
  const popupRef = useRef(null);
  const popupNavigatedRef = useRef(false);

  // Polls the in-flight order; stops on terminal saga state.
  const { data: pendingOrder } = useOrder(pendingOrderId, {
    polling: pendingOrderId != null,
  });

  // Default-select an address
  useEffect(() => {
    if (!addresses || addresses.length === 0) return;
    if (shippingId == null) {
      const def = addresses.find((a) => a.isDefault) ?? addresses[0];
      setShippingId(def.id);
    }
    if (!billingSameAsShipping && billingId == null) {
      const def = addresses.find((a) => a.isDefault) ?? addresses[0];
      setBillingId(def.id);
    }
  }, [addresses, shippingId, billingId, billingSameAsShipping]);

  const shippingAddress = addresses?.find((a) => a.id === shippingId) ?? null;
  const billingAddress = billingSameAsShipping
    ? shippingAddress
    : addresses?.find((a) => a.id === billingId) ?? null;

  const discountAmount = useMemo(
    () => computeCartDiscount(subtotalAmount, items.length, appliedPromotion),
    [subtotalAmount, items.length, appliedPromotion],
  );
  const totalAmount = Math.max(0, subtotalAmount - discountAmount);

  // ---- Payment flow effects ----

  // 1. Once order has paymentPageUrl, navigate the popup to it.
  useEffect(() => {
    if (!pendingOrder?.paymentPageUrl) return;
    if (popupNavigatedRef.current) return;
    const popup = popupRef.current;
    if (!popup || popup.closed) return;
    popup.location.href = pendingOrder.paymentPageUrl;
    popupNavigatedRef.current = true;
  }, [pendingOrder]);

  // 2. When saga reaches terminal state, finish the flow.
  useEffect(() => {
    if (!pendingOrder) return;
    if (!isTerminalSagaState(pendingOrder.sagaState)) return;

    const popup = popupRef.current;
    if (popup && !popup.closed) popup.close();

    if (pendingOrder.sagaState === 'COMPLETED') {
      // Order placed successfully — drop cart contents (silently; the
      // success page is the user-facing feedback, no toast needed).
      clearCartApi()
        .then((empty) => queryClient.setQueryData(['cart'], empty))
        .catch(() => {
          // Best-effort. If it fails the user can clear manually later.
          queryClient.invalidateQueries({ queryKey: ['cart'] });
        });
      navigate(`/orders/${pendingOrder.id}`, { replace: true });
    } else {
      toast.error(pendingOrder.failureReason ?? 'Order failed. Please try again.');
      resetPaymentFlow();
    }
  }, [pendingOrder, navigate]);

    // 3. Watch the popup; if user closes it before payment completes, reset.
  useEffect(() => {
    if (pendingOrderId == null) return;
    const popup = popupRef.current;
    if (!popup) return;

    let popupReturned = false;
    const channel = new BroadcastChannel('payment-flow');
    channel.onmessage = (e) => {
      if (e.data?.type === 'popup-returned') popupReturned = true;
    };

    const interval = setInterval(() => {
      if (popup.closed) {
        clearInterval(interval);
        if (popupReturned) return; // polling will catch terminal state
        if (!pendingOrder || !isTerminalSagaState(pendingOrder.sagaState)) {
          toast.info('Payment window closed. You can try again.');
          resetPaymentFlow();
        }
      }
    }, 500);

    return () => {
      clearInterval(interval);
      channel.close();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pendingOrderId, pendingOrder?.sagaState]);

  function resetPaymentFlow() {
    setPendingOrderId(null);
    popupRef.current = null;
    popupNavigatedRef.current = false;
  }

  // ---- Handlers ----

  const handleApplyCoupon = async (e) => {
    e.preventDefault();
    const code = couponInput.trim();
    if (!code) return;
    if (subtotalAmount <= 0) {
      toast.error('Add items to your cart before applying a coupon.');
      return;
    }
    try {
      const result = await validate.mutateAsync({
        code,
        cartTotal: subtotalAmount,
        currency,
      });
      if (result.valid) {
        setAppliedPromotion(result);
        setCouponInput('');
        toast.success(`${result.code} applied`);
      } else {
        setAppliedPromotion(null);
        toast.error(describePromotionFailure(result));
      }
    } catch (err) {
      toast.error(extractErrorMessage(err));
    }
  };

  const handleRemoveCoupon = () => {
    setAppliedPromotion(null);
  };

  const handleAddressCreated = async (payload) => {
    const created = await createAddress.mutateAsync(payload);
    setShippingId(created.id);
    if (!billingSameAsShipping) {
      setBillingId(created.id);
    }
    setShowAddForm(false);
  };

  const handlePlaceOrder = async () => {
    if (!shippingAddress) {
      toast.error('Please select a shipping address.');
      return;
    }
    if (!billingAddress) {
      toast.error('Please select a billing address.');
      return;
    }
    if (items.length === 0) {
      toast.error('Your cart is empty.');
      return;
    }

    // Open the popup synchronously inside the click handler so the
    // browser doesn't block it. Point to a placeholder until the
    // backend gives us the Iyzico URL.
    const popup = window.open(
      'about:blank',
      'iyzico-payment',
      'width=520,height=720,resizable=yes,scrollbars=yes',
    );
    if (!popup) {
      toast.error('Please allow popups to complete payment.');
      return;
    }
    try {
      popup.document.write(
        '<!doctype html><html><head><title>Preparing payment</title>' +
          '<style>body{font-family:system-ui;display:flex;align-items:center;' +
          'justify-content:center;height:100vh;color:#666;margin:0}</style></head>' +
          '<body><p>Preparing payment...</p></body></html>',
      );
    } catch {
      // Some browsers block document.write on cross-origin redirects.
    }
    popupRef.current = popup;
    popupNavigatedRef.current = false;

    const payload = {
      lineItems: items.map((it) => ({
        productId: it.productId,
        quantity: it.quantity,
      })),
      appliedCouponCode: appliedPromotion?.code ?? null,
      shippingAddress: stripAddressForOrder(shippingAddress),
      billingAddress: stripAddressForOrder(billingAddress),
      paymentProvider: 'iyzico',
      returnUrl: `${window.location.origin}/payments/return`,
    };

    try {
      const order = await placeOrder.mutateAsync(payload);
      sessionStorage.setItem('ecom.checkout.lastOrderId', String(order.id));
      setPendingOrderId(order.id);
      // The effects above take it from here:
      // 1. wait for paymentPageUrl → navigate popup
      // 2. wait for saga terminal → close popup + navigate to /orders/{id}
    } catch (err) {
      if (popup && !popup.closed) popup.close();
      popupRef.current = null;
      toast.error(extractErrorMessage(err));
    }
  };

  if (cartLoading || addressesLoading) {
    return (
      <section className="container py-8">
        <Skeleton className="h-8 w-48" />
        <div className="mt-6 grid gap-6 lg:grid-cols-[minmax(0,1fr)_400px]">
          <Skeleton className="h-96" />
          <Skeleton className="h-72" />
        </div>
      </section>
    );
  }

  if (items.length === 0 && pendingOrderId == null) {
    return (
      <section className="container py-16">
        <div className="mx-auto max-w-md rounded-lg border bg-card p-8 text-center shadow-sm">
          <ShoppingCart className="mx-auto h-12 w-12 text-muted-foreground" />
          <h1 className="mt-4 text-xl font-semibold">Your cart is empty</h1>
          <p className="mt-2 text-sm text-muted-foreground">
            Add items before checking out.
          </p>
          <Button asChild className="mt-6">
            <Link to="/">Continue shopping</Link>
          </Button>
        </div>
      </section>
    );
  }

  const paymentInProgress = pendingOrderId != null;
  const placeOrderDisabled =
    placeOrder.isPending ||
    paymentInProgress ||
    !shippingAddress ||
    !billingAddress;

  // Status hint shown next to the button while payment is in flight.
  let paymentStatusHint = null;
  if (paymentInProgress) {
    if (!pendingOrder?.paymentPageUrl && !popupNavigatedRef.current) {
      paymentStatusHint = 'Setting up payment...';
    } else {
      paymentStatusHint = 'Waiting for payment to complete in the popup window...';
    }
  }

  return (
    <section className="container py-8">
      <h1 className="text-2xl font-bold tracking-tight">Checkout</h1>

      <div className="mt-6 grid gap-6 lg:grid-cols-[minmax(0,1fr)_400px]">
        <div className="space-y-6">
          {/* Shipping address */}
          <Section
            title="Shipping address"
            icon={<MapPin className="h-5 w-5" />}
          >
            {addresses && addresses.length > 0 && !showAddForm ? (
              <>
                <div className="space-y-3">
                  {addresses.map((addr) => (
                    <AddressCard
                      key={addr.id}
                      address={addr}
                      selectable
                      selected={addr.id === shippingId}
                      onSelect={(a) => setShippingId(a.id)}
                    />
                  ))}
                </div>
                <Button
                  type="button"
                  variant="outline"
                  className="mt-4 w-full"
                  onClick={() => setShowAddForm(true)}
                >
                  <Plus className="mr-2 h-4 w-4" />
                  Add a new address
                </Button>
              </>
            ) : (
              <div className="space-y-3">
                {addresses && addresses.length === 0 && (
                  <p className="text-sm text-muted-foreground">
                    Add an address to continue.
                  </p>
                )}
                <AddressForm
                  onSubmit={handleAddressCreated}
                  onCancel={
                    addresses && addresses.length > 0
                      ? () => setShowAddForm(false)
                      : undefined
                  }
                  busy={createAddress.isPending}
                  submitLabel="Save and use"
                />
              </div>
            )}
          </Section>

          {/* Billing */}
          {addresses && addresses.length > 0 && (
            <Section title="Billing address" icon={<MapPin className="h-5 w-5" />}>
              <label className="mb-4 flex items-center gap-2 text-sm">
                <input
                  type="checkbox"
                  checked={billingSameAsShipping}
                  onChange={(e) => setBillingSameAsShipping(e.target.checked)}
                  className="h-4 w-4 rounded border-input text-primary focus:ring-2 focus:ring-ring"
                />
                Same as shipping address
              </label>
              {!billingSameAsShipping && (
                <div className="space-y-3">
                  {addresses.map((addr) => (
                    <AddressCard
                      key={addr.id}
                      address={addr}
                      selectable
                      selected={addr.id === billingId}
                      onSelect={(a) => setBillingId(a.id)}
                    />
                  ))}
                </div>
              )}
            </Section>
          )}

          {/* Coupon */}
          <Section title="Coupon code" icon={<Tag className="h-5 w-5" />}>
            {appliedPromotion ? (
              <div className="flex items-center justify-between rounded-md border border-emerald-200 bg-emerald-50 px-4 py-3">
                <div className="flex items-center gap-2">
                  <CheckCircle2 className="h-5 w-5 text-emerald-600" />
                  <div>
                    <p className="text-sm font-semibold text-emerald-900">
                      {appliedPromotion.code}
                    </p>
                    <p className="text-xs text-emerald-800">
                      {describePromotion(appliedPromotion)}
                    </p>
                  </div>
                </div>
                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  className="h-8 w-8 text-emerald-900 hover:bg-emerald-100"
                  onClick={handleRemoveCoupon}
                  aria-label="Remove coupon"
                >
                  <X className="h-4 w-4" />
                </Button>
              </div>
            ) : (
              <form onSubmit={handleApplyCoupon} className="flex gap-2">
                <Input
                  value={couponInput}
                  onChange={(e) => setCouponInput(e.target.value.toUpperCase())}
                  placeholder="Enter coupon code"
                  className="font-mono"
                />
                <Button type="submit" disabled={validate.isPending} variant="secondary">
                  {validate.isPending ? 'Checking...' : 'Apply'}
                </Button>
              </form>
            )}
          </Section>
        </div>

        {/* Order summary */}
        <aside className="space-y-4 self-start rounded-lg border bg-card p-6 shadow-sm lg:sticky lg:top-24">
          <h2 className="text-lg font-semibold">Order summary</h2>

          <ul className="space-y-3 border-b pb-4">
            {items.map((it) => (
              <li key={it.itemId ?? it.productId} className="flex gap-3">
                <div className="h-12 w-12 shrink-0 overflow-hidden rounded border bg-muted">
                  {it.productImageUrl ? (
                    <img
                      src={it.productImageUrl}
                      alt={it.productName}
                      className="h-full w-full object-cover"
                    />
                  ) : (
                    <div className="flex h-full w-full items-center justify-center text-muted-foreground">
                      <ImageOff className="h-5 w-5" />
                    </div>
                  )}
                </div>
                <div className="flex flex-1 items-center justify-between gap-2">
                  <div className="min-w-0">
                    <p className="line-clamp-1 text-sm font-medium">{it.productName}</p>
                    <p className="text-xs text-muted-foreground">Qty {it.quantity}</p>
                  </div>
                  <PriceTag money={it.lineTotal} size="sm" />
                </div>
              </li>
            ))}
          </ul>

          <dl className="space-y-2 text-sm">
            <div className="flex items-center justify-between">
              <dt className="text-muted-foreground">Subtotal</dt>
              <dd>
                <PriceTag money={cart.subtotal} size="sm" />
              </dd>
            </div>
            {appliedPromotion && discountAmount > 0 && (
              <div className="flex items-center justify-between">
                <dt className="text-muted-foreground">
                  Discount{' '}
                  <span className="font-mono text-xs uppercase text-emerald-700">
                    {appliedPromotion.code}
                  </span>
                </dt>
                <dd className="font-medium text-emerald-700">
                  − {formatMoney({ amount: discountAmount, currency })}
                </dd>
              </div>
            )}
            <div className="flex items-center justify-between">
              <dt className="text-muted-foreground">Shipping</dt>
              <dd className="text-muted-foreground">Free</dd>
            </div>
          </dl>

          <div className="flex items-center justify-between border-t pt-4">
            <span className="text-base font-semibold">Total</span>
            <PriceTag money={{ amount: totalAmount, currency }} size="lg" />
          </div>

          <Button
            size="lg"
            className="w-full"
            onClick={handlePlaceOrder}
            disabled={placeOrderDisabled}
          >
            {paymentInProgress ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                {placeOrder.isPending ? 'Placing order...' : 'Waiting for payment...'}
              </>
            ) : (
              'Place order'
            )}
          </Button>
          {paymentStatusHint ? (
            <p className="text-center text-xs text-muted-foreground">
              {paymentStatusHint}
            </p>
          ) : (
            <p className="text-center text-xs text-muted-foreground">
              Payment opens in a popup window.
            </p>
          )}
        </aside>
      </div>
    </section>
  );
}

function Section({ title, icon, children }) {
  return (
    <div className="rounded-lg border bg-card p-6 shadow-sm">
      <h2 className="mb-4 flex items-center gap-2 text-lg font-semibold">
        {icon}
        {title}
      </h2>
      {children}
    </div>
  );
}

function stripAddressForOrder(addr) {
  return {
    label: addr.label || undefined,
    recipientName: addr.recipientName,
    phoneNumber: addr.phoneNumber,
    line1: addr.line1,
    line2: addr.line2 || undefined,
    city: addr.city,
    postalCode: addr.postalCode,
    country: addr.country,
  };
}
