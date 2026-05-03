import { Link } from 'react-router-dom';
import { ImageOff } from 'lucide-react';
import { PriceTag } from './PriceTag';
import { StockBadge } from './StockBadge';

export function ProductCard({ product, stockUnavailable }) {
  return (
    <Link
      to={`/p/${product.id}`}
      className="group block overflow-hidden rounded-lg border bg-card transition-shadow hover:shadow-md focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
    >
      <div className="relative aspect-square overflow-hidden bg-muted">
        {product.primaryImageUrl ? (
          <img
            src={product.primaryImageUrl}
            alt={product.name}
            loading="lazy"
            className="h-full w-full object-cover transition-transform duration-200 group-hover:scale-105"
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center text-muted-foreground">
            <ImageOff className="h-10 w-10" />
          </div>
        )}
      </div>
      <div className="space-y-2 p-4">
        {product.brand && (
          <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
            {product.brand}
          </p>
        )}
        <h3 className="line-clamp-2 min-h-[2.5rem] text-sm font-medium text-foreground">
          {product.name}
        </h3>
        <div className="flex items-center justify-between gap-2">
          <PriceTag money={product.listPrice} />
          <StockBadge status={product.stockStatus} unavailable={stockUnavailable} />
        </div>
      </div>
    </Link>
  );
}
