import { useState } from 'react';
import { ShoppingCart } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { CartSheet } from './CartSheet';
import { useCartItemCount } from '@/hooks/useCart';

export function CartButton() {
  const [open, setOpen] = useState(false);
  const count = useCartItemCount();

  return (
    <>
      <Button
        variant="ghost"
        size="icon"
        className="relative h-10 w-10"
        onClick={() => setOpen(true)}
        aria-label={count > 0 ? `Open cart, ${count} items` : 'Open cart'}
      >
        <ShoppingCart className="h-5 w-5" />
        {count > 0 && (
          <span className="absolute -right-0.5 -top-0.5 inline-flex h-5 min-w-[1.25rem] items-center justify-center rounded-full bg-primary px-1 text-[10px] font-bold text-primary-foreground">
            {count > 99 ? '99+' : count}
          </span>
        )}
      </Button>
      <CartSheet open={open} onOpenChange={setOpen} />
    </>
  );
}
