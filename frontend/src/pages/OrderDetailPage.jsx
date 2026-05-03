import { Link, useParams } from 'react-router-dom';
import { CheckCircle2, ImageOff, Loader2, MapPin, XCircle } from 'lucide-react';
import { useOrder } from '@/hooks/useOrder';
import { usePageTitle } from '@/hooks/usePageTitle';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { PriceTag } from '@/components/product/PriceTag';
import { describeSagaState } from '@/lib/sagaState';
import { extractErrorMessage } from '@/api/client';

export function OrderDetailPage() {
  const { orderId } = useParams();
  const { data: order, isLoading, error } = useOrder(orderId, { polling: true });
  usePageTitle(order ? `Order #${order.id}` : 'Order');

  if (isLoading) {
    return (
      <section className="container py-8">
        <Skeleton className="h-8 w-64" />
        <div className="mt-6 grid gap-6 lg:grid-cols-[minmax(0,1fr)_360px]">
          <Skeleton className="h-96" />
          <Skeleton className="h-72" />
        </div>
      </section>
    );
  }

  if (error) {
    return (
      <section className="container py-12">
        <div className="rounded-md border border-destructive/30 bg-destructive/5 p-6 text-center text-sm text-destructive">
          {extractErrorMessage(error)}
        </div>
      </section>
    );
  }

  if (!order) return null;

  const stateInfo = describeSagaState(order.sagaState);
  const isFailed = stateInfo.stage === 'failed';
  const isDone = stateInfo.stage === 'done';
  const inProgress = !stateInfo.terminal;

  return (
    <section className="container py-8">
      <div className="flex flex-wrap items-baseline justify-between gap-3">
        <h1 className="text-2xl font-bold tracking-tight">
          Order #{order.id}
        </h1>
        <Badge variant={stateInfo.variant} className="gap-1.5">
          {isDone && <CheckCircle2 className="h-3.5 w-3.5" />}
          {isFailed && <XCircle className="h-3.5 w-3.5" />}
          {inProgress && <Loader2 className="h-3.5 w-3.5 animate-spin" />}
          {stateInfo.label}
        </Badge>
      </div>

      {/* Banner */}
      {isDone && (
        <div className="mt-4 rounded-md border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-900">
          Thank you! Your payment was successful and your order is on its way.
        </div>
      )}
      {isFailed && (
        <div className="mt-4 rounded-md border border-destructive/30 bg-destructive/5 px-4 py-3 text-sm text-destructive">
          {order.failureReason ?? 'Something went wrong with your order.'}
          <div className="mt-2">
            <Button asChild variant="outline" size="sm">
              <Link to="/cart">Back to cart</Link>
            </Button>
          </div>
        </div>
      )}
      {inProgress && (
        <div className="mt-4 flex items-center gap-2 rounded-md border bg-muted/40 px-4 py-3 text-sm text-muted-foreground">
          <Loader2 className="h-4 w-4 animate-spin" />
          {stateInfo.stage === 'payment'
            ? 'Confirming your payment...'
            : 'Processing your order...'}
        </div>
      )}

      <div className="mt-6 grid gap-6 lg:grid-cols-[minmax(0,1fr)_360px]">
        {/* Line items */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-lg font-semibold">Items</h2>
          <ul className="space-y-4">
            {order.lineItems?.map((item) => (
              <li
                key={item.id ?? item.productId}
                className="flex gap-4 border-b pb-4 last:border-b-0 last:pb-0"
              >
                <div className="h-16 w-16 shrink-0 overflow-hidden rounded border bg-muted">
                  {item.primaryImageUrl ? (
                    <img
                      src={item.primaryImageUrl}
                      alt={item.productName}
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
                    {item.productBrand && (
                      <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                        {item.productBrand}
                      </p>
                    )}
                    <p className="text-sm font-medium">{item.productName}</p>
                    <p className="text-xs text-muted-foreground">Qty {item.quantity}</p>
                  </div>
                  <PriceTag money={item.lineTotal} size="sm" />
                </div>
              </li>
            ))}
          </ul>
        </div>

        {/* Summary + addresses */}
        <aside className="space-y-4 self-start">
          <div className="rounded-lg border bg-card p-6 shadow-sm">
            <h2 className="mb-3 text-lg font-semibold">Summary</h2>
            <dl className="space-y-2 text-sm">
              <Row label="Subtotal" value={<PriceTag money={order.subtotal} size="sm" />} />
              {order.cartTotalDiscount?.amount > 0 && (
                <Row
                  label={
                    <>
                      Discount{' '}
                      {order.appliedCouponCode && (
                        <span className="font-mono text-xs uppercase text-emerald-700">
                          {order.appliedCouponCode}
                        </span>
                      )}
                    </>
                  }
                  value={
                    <span className="font-medium text-emerald-700">
                      − <PriceTag money={order.cartTotalDiscount} size="sm" />
                    </span>
                  }
                />
              )}
              <Row label="Shipping" value={<span className="text-muted-foreground">Free</span>} />
            </dl>
            <div className="mt-4 flex items-center justify-between border-t pt-3">
              <span className="text-base font-semibold">Total</span>
              <PriceTag money={order.total} size="lg" />
            </div>
          </div>

          {order.shippingAddress && (
            <div className="rounded-lg border bg-card p-6 shadow-sm">
              <h2 className="mb-3 flex items-center gap-2 text-sm font-semibold">
                <MapPin className="h-4 w-4" /> Shipping address
              </h2>
              <AddressLines a={order.shippingAddress} />
            </div>
          )}

          {order.billingAddress &&
            !sameAddress(order.billingAddress, order.shippingAddress) && (
              <div className="rounded-lg border bg-card p-6 shadow-sm">
                <h2 className="mb-3 flex items-center gap-2 text-sm font-semibold">
                  <MapPin className="h-4 w-4" /> Billing address
                </h2>
                <AddressLines a={order.billingAddress} />
              </div>
            )}
        </aside>
      </div>

      <div className="mt-8">
        <Button asChild variant="ghost">
          <Link to="/">Continue shopping</Link>
        </Button>
      </div>
    </section>
  );
}

function Row({ label, value }) {
  return (
    <div className="flex items-center justify-between">
      <dt className="text-muted-foreground">{label}</dt>
      <dd>{value}</dd>
    </div>
  );
}

function AddressLines({ a }) {
  return (
    <div className="space-y-0.5 text-sm text-muted-foreground">
      <p className="text-foreground">{a.recipientName}</p>
      <p>
        {a.line1}
        {a.line2 ? `, ${a.line2}` : ''}
      </p>
      <p>
        {a.postalCode} {a.city}, {a.country}
      </p>
      {a.phoneNumber && <p>{a.phoneNumber}</p>}
    </div>
  );
}

function sameAddress(a, b) {
  if (!a || !b) return false;
  return (
    a.line1 === b.line1 &&
    a.line2 === b.line2 &&
    a.postalCode === b.postalCode &&
    a.city === b.city &&
    a.country === b.country &&
    a.recipientName === b.recipientName
  );
}
