import { Link } from 'react-router-dom';
import { ImageOff, Minus, Plus, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { PriceTag } from '@/components/product/PriceTag';
import { useRemoveCartItem, useUpdateCartItem } from '@/hooks/useCart';

export function CartLineItem({ item, onNavigate }) {
  const update = useUpdateCartItem();
  const remove = useRemoveCartItem();

  const decrement = () => {
    if (item.quantity <= 1) {
      remove.mutate({ productId: item.productId });
    } else {
      update.mutate({ productId: item.productId, quantity: item.quantity - 1 });
    }
  };

  const increment = () =>
    update.mutate({ productId: item.productId, quantity: item.quantity + 1 });

  const handleRemove = () => remove.mutate({ productId: item.productId });

  const busy = update.isPending || remove.isPending;

  return (
    <div className="flex gap-3 border-b py-4 last:border-b-0">
      <Link
        to={`/p/${item.productId}`}
        onClick={onNavigate}
        className="block h-20 w-20 shrink-0 overflow-hidden rounded-md border bg-muted"
      >
        {item.productImageUrl ? (
          <img
            src={item.productImageUrl}
            alt={item.productName}
            className="h-full w-full object-cover"
            loading="lazy"
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center text-muted-foreground">
            <ImageOff className="h-6 w-6" />
          </div>
        )}
      </Link>

      <div className="flex flex-1 flex-col gap-2">
        <div className="flex items-start justify-between gap-2">
          <div className="min-w-0">
            {item.productBrand && (
              <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                {item.productBrand}
              </p>
            )}
            <Link
              to={`/p/${item.productId}`}
              onClick={onNavigate}
              className="line-clamp-2 text-sm font-medium hover:text-primary"
            >
              {item.productName}
            </Link>
          </div>
          <Button
            variant="ghost"
            size="icon"
            className="h-7 w-7 text-muted-foreground hover:text-destructive"
            onClick={handleRemove}
            disabled={busy}
            aria-label="Remove item"
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex items-center justify-between">
          <div className="inline-flex items-center rounded-md border">
            <button
              type="button"
              className="flex h-7 w-7 items-center justify-center text-muted-foreground hover:text-foreground disabled:opacity-50"
              onClick={decrement}
              disabled={busy}
              aria-label="Decrease quantity"
            >
              <Minus className="h-3.5 w-3.5" />
            </button>
            <span className="min-w-[2rem] text-center text-sm font-medium">
              {item.quantity}
            </span>
            <button
              type="button"
              className="flex h-7 w-7 items-center justify-center text-muted-foreground hover:text-foreground disabled:opacity-50"
              onClick={increment}
              disabled={busy}
              aria-label="Increase quantity"
            >
              <Plus className="h-3.5 w-3.5" />
            </button>
          </div>
          <PriceTag money={item.lineTotal} size="sm" />
        </div>
      </div>
    </div>
  );
}
