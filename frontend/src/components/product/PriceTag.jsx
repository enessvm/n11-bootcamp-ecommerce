import { cn } from '@/lib/utils';
import { formatMoney } from '@/lib/format';

export function PriceTag({ money, className, size = 'md' }) {
  const sizeClass = {
    sm: 'text-sm font-semibold',
    md: 'text-base font-semibold',
    lg: 'text-2xl font-bold',
  }[size];
  return (
    <span className={cn('text-foreground', sizeClass, className)}>
      {formatMoney(money) || '—'}
    </span>
  );
}
