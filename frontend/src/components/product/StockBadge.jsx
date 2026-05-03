import { Badge } from '@/components/ui/badge';
import { STOCK_LABELS } from '@/lib/format';

const VARIANT_BY_STATUS = {
  IN_STOCK: 'success',
  LOW_STOCK: 'warning',
  OUT_OF_STOCK: 'destructive',
};

export function StockBadge({ status, unavailable }) {
  if (unavailable || !status) return null;
  return (
    <Badge variant={VARIANT_BY_STATUS[status] ?? 'secondary'}>
      {STOCK_LABELS[status] ?? status}
    </Badge>
  );
}
