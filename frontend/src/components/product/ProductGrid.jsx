import { ProductCard } from './ProductCard';
import { Skeleton } from '@/components/ui/skeleton';
import { extractErrorMessage } from '@/api/client';

export function ProductGrid({ products, isLoading, error, stockUnavailable, emptyMessage }) {
  if (isLoading) {
    return (
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
        {Array.from({ length: 8 }).map((_, i) => (
          <ProductCardSkeleton key={i} />
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-md border border-destructive/30 bg-destructive/5 p-6 text-center">
        <p className="text-sm text-destructive">
          Could not load products: {extractErrorMessage(error)}
        </p>
      </div>
    );
  }

  if (!products || products.length === 0) {
    return (
      <div className="rounded-md border bg-muted/30 p-12 text-center">
        <p className="text-sm text-muted-foreground">
          {emptyMessage || 'No products to show.'}
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
      {products.map((product) => (
        <ProductCard
          key={product.id}
          product={product}
          stockUnavailable={stockUnavailable}
        />
      ))}
    </div>
  );
}

function ProductCardSkeleton() {
  return (
    <div className="overflow-hidden rounded-lg border bg-card">
      <Skeleton className="aspect-square rounded-none" />
      <div className="space-y-2 p-4">
        <Skeleton className="h-3 w-1/3" />
        <Skeleton className="h-4 w-full" />
        <Skeleton className="h-4 w-2/3" />
        <div className="flex items-center justify-between pt-2">
          <Skeleton className="h-5 w-16" />
          <Skeleton className="h-5 w-14 rounded-full" />
        </div>
      </div>
    </div>
  );
}
