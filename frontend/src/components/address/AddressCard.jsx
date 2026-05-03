import { Pencil, Star, Trash2 } from 'lucide-react';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

/**
 * Renders a single saved address. Reused on the checkout page (selectable
 * radio-card behaviour) and the account/addresses page (with edit & delete).
 *
 * Props:
 *  - address: AddressResponse from the API
 *  - selected: boolean — show the "selected" outline (only when selectable)
 *  - selectable: boolean — whole card becomes a click target
 *  - onSelect(address)
 *  - onEdit(address) — shows pencil button
 *  - onDelete(address) — shows trash button
 *  - busy: boolean — disables delete while a mutation is in flight
 */
export function AddressCard({
  address,
  selected,
  selectable,
  onSelect,
  onEdit,
  onDelete,
  busy,
}) {
  const handleClick = selectable && onSelect ? () => onSelect(address) : undefined;

  return (
    <div
      onClick={handleClick}
      className={cn(
        'rounded-lg border bg-card p-4 transition-colors',
        selectable && 'cursor-pointer hover:border-primary',
        selected && 'border-primary ring-2 ring-primary',
      )}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0 space-y-1">
          <div className="flex items-center gap-2">
            <p className="font-medium text-foreground">
              {address.label || 'Address'}
            </p>
            {address.isDefault && (
              <Badge variant="secondary" className="gap-1">
                <Star className="h-3 w-3 fill-current" />
                Default
              </Badge>
            )}
          </div>
          <p className="text-sm text-foreground">{address.recipientName}</p>
          <p className="text-sm text-muted-foreground">
            {address.line1}
            {address.line2 ? `, ${address.line2}` : ''}
          </p>
          <p className="text-sm text-muted-foreground">
            {address.postalCode} {address.city}, {address.country}
          </p>
          {address.phoneNumber && (
            <p className="text-sm text-muted-foreground">{address.phoneNumber}</p>
          )}
        </div>

        {(onEdit || onDelete) && (
          <div className="flex shrink-0 gap-1">
            {onEdit && (
              <Button
                type="button"
                variant="ghost"
                size="icon"
                className="h-8 w-8 text-muted-foreground hover:text-foreground"
                onClick={(e) => {
                  e.stopPropagation();
                  onEdit(address);
                }}
                aria-label="Edit address"
              >
                <Pencil className="h-4 w-4" />
              </Button>
            )}
            {onDelete && (
              <Button
                type="button"
                variant="ghost"
                size="icon"
                className="h-8 w-8 text-muted-foreground hover:text-destructive"
                onClick={(e) => {
                  e.stopPropagation();
                  onDelete(address);
                }}
                disabled={busy}
                aria-label="Delete address"
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
