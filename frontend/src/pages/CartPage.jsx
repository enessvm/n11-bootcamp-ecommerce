import { Link } from 'react-router-dom';
import { ShoppingCart, Trash2 } from 'lucide-react';
import { useAuth } from '@/auth/useAuth';
import { useCart, useClearCart } from '@/hooks/useCart';
import { usePageTitle } from '@/hooks/usePageTitle';
import { CartLineItem } from '@/components/cart/CartLineItem';
import { PriceTag } from '@/components/product/PriceTag';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { extractErrorMessage } from '@/api/client';

export function CartPage() {
  usePageTitle('Cart');
  const { isAuthenticated } = useAuth();
  const { data: cart, isLoading, error } = useCart();
  const clearMutation = useClearCart();

  if (!isAuthenticated) {
    return (
      <section className="container py-16">
        <div className="mx-auto max-w-md rounded-lg border bg-card p-8 text-center shadow-sm">
          <ShoppingCart className="mx-auto h-12 w-12 text-muted-foreground" />
          <h1 className="mt-4 text-xl font-semibold">Sign in to view your cart</h1>
          <p className="mt-2 text-sm text-muted-foreground">
            Your cart items are tied to your account.
          </p>
          <Button asChild className="mt-6">
            <Link to="/login?returnTo=/cart">Sign in</Link>
          </Button>
        </div>
      </section>
    );
  }

  if (isLoading) {
    return (
      <section className="container py-8">
        <Skeleton className="h-8 w-40" />
        <div className="mt-6 grid gap-6 lg:grid-cols-[minmax(0,1fr)_360px]">
          <div className="space-y-4">
            <Skeleton className="h-24 w-full" />
            <Skeleton className="h-24 w-full" />
          </div>
          <Skeleton className="h-48 w-full" />
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

  const items = cart?.items ?? [];

  if (items.length === 0) {
    return (
      <section className="container py-16">
        <div className="mx-auto max-w-md rounded-lg border bg-card p-8 text-center shadow-sm">
          <ShoppingCart className="mx-auto h-12 w-12 text-muted-foreground" />
          <h1 className="mt-4 text-xl font-semibold">Your cart is empty</h1>
          <p className="mt-2 text-sm text-muted-foreground">
            Add items from the storefront and they'll appear here.
          </p>
          <Button asChild className="mt-6">
            <Link to="/">Continue shopping</Link>
          </Button>
        </div>
      </section>
    );
  }

  return (
    <section className="container py-8">
      <div className="flex items-baseline justify-between">
        <h1 className="text-2xl font-bold tracking-tight">
          Shopping cart{' '}
          <span className="text-base font-normal text-muted-foreground">
            ({items.length} {items.length === 1 ? 'item' : 'items'})
          </span>
        </h1>
        <Button
          variant="ghost"
          size="sm"
          onClick={() => clearMutation.mutate()}
          disabled={clearMutation.isPending}
          className="text-muted-foreground hover:text-destructive"
        >
          <Trash2 className="mr-1.5 h-4 w-4" />
          Clear cart
        </Button>
      </div>

      <div className="mt-6 grid gap-6 lg:grid-cols-[minmax(0,1fr)_360px]">
        <div className="rounded-lg border bg-card px-6 shadow-sm">
          {items.map((item) => (
            <CartLineItem key={item.itemId ?? item.productId} item={item} />
          ))}
        </div>

        <aside className="space-y-4 self-start rounded-lg border bg-card p-6 shadow-sm lg:sticky lg:top-24">
          <h2 className="text-lg font-semibold">Order summary</h2>

          <dl className="space-y-2 text-sm">
            <div className="flex items-center justify-between">
              <dt className="text-muted-foreground">Subtotal</dt>
              <dd>
                <PriceTag money={cart.subtotal} size="sm" />
              </dd>
            </div>
            <div className="flex items-center justify-between">
              <dt className="text-muted-foreground">Shipping</dt>
              <dd className="text-muted-foreground">Calculated at checkout</dd>
            </div>
          </dl>

          <div className="flex items-center justify-between border-t pt-4">
            <span className="text-base font-semibold">Total</span>
            <PriceTag money={cart.subtotal} size="lg" />
          </div>

          <Button asChild size="lg" className="w-full">
            <Link to="/checkout">Proceed to checkout</Link>
          </Button>
          <p className="text-center text-xs text-muted-foreground">
            You'll review address and payment on the next step.
          </p>
        </aside>
      </div>
    </section>
  );
}
