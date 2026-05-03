import { Link } from 'react-router-dom';
import { ShoppingCart } from 'lucide-react';
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetClose,
} from '@/components/ui/sheet';
import { Button } from '@/components/ui/button';
import { CartLineItem } from './CartLineItem';
import { PriceTag } from '@/components/product/PriceTag';
import { useAuth } from '@/auth/useAuth';
import { useCart } from '@/hooks/useCart';
import { extractErrorMessage } from '@/api/client';

export function CartSheet({ open, onOpenChange }) {
  const { isAuthenticated } = useAuth();
  const { data: cart, isLoading, error } = useCart();
  const handleClose = () => onOpenChange(false);

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent>
        <SheetHeader>
          <SheetTitle>Your cart</SheetTitle>
          <SheetDescription className="sr-only">
            Review and adjust the items in your shopping cart.
          </SheetDescription>
        </SheetHeader>

        <div className="flex-1 overflow-y-auto px-6">
          {!isAuthenticated ? (
            <EmptyState
              icon={<ShoppingCart className="h-12 w-12" />}
              title="Sign in to view your cart"
              description="Your cart is saved across devices."
              action={
                <SheetClose asChild>
                  <Button asChild>
                    <Link to="/login">Sign in</Link>
                  </Button>
                </SheetClose>
              }
            />
          ) : isLoading ? (
            <p className="py-12 text-center text-sm text-muted-foreground">Loading cart...</p>
          ) : error ? (
            <p className="py-12 text-center text-sm text-destructive">
              {extractErrorMessage(error)}
            </p>
          ) : !cart?.items || cart.items.length === 0 ? (
            <EmptyState
              icon={<ShoppingCart className="h-12 w-12" />}
              title="Your cart is empty"
              description="Browse the storefront and add items you like."
              action={
                <SheetClose asChild>
                  <Button asChild variant="secondary">
                    <Link to="/">Continue shopping</Link>
                  </Button>
                </SheetClose>
              }
            />
          ) : (
            <div>
              {cart.items.map((item) => (
                <CartLineItem
                  key={item.itemId ?? item.productId}
                  item={item}
                  onNavigate={handleClose}
                />
              ))}
            </div>
          )}
        </div>

        {isAuthenticated && cart?.items && cart.items.length > 0 && (
          <SheetFooter className="flex-col gap-3 sm:flex-col sm:items-stretch">
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-muted-foreground">Subtotal</span>
              <PriceTag money={cart.subtotal} size="lg" />
            </div>
            <div className="flex flex-col gap-2 sm:flex-row">
              <SheetClose asChild>
                <Button asChild variant="outline" className="flex-1">
                  <Link to="/cart">View cart</Link>
                </Button>
              </SheetClose>
              <SheetClose asChild>
                <Button asChild className="flex-1">
                  <Link to="/checkout">Checkout</Link>
                </Button>
              </SheetClose>
            </div>
          </SheetFooter>
        )}
      </SheetContent>
    </Sheet>
  );
}

function EmptyState({ icon, title, description, action }) {
  return (
    <div className="flex flex-col items-center gap-3 py-16 text-center">
      <div className="text-muted-foreground">{icon}</div>
      <div>
        <p className="font-medium">{title}</p>
        <p className="mt-1 text-sm text-muted-foreground">{description}</p>
      </div>
      {action}
    </div>
  );
}
