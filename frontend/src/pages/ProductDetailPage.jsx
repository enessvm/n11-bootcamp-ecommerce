import { useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { ImageOff, ShoppingCart } from 'lucide-react';
import { useProduct } from '@/hooks/useProduct';
import { useAddCartItem } from '@/hooks/useCart';
import { usePageTitle } from '@/hooks/usePageTitle';
import { useAuth } from '@/auth/useAuth';
import { Breadcrumb } from '@/components/category/Breadcrumb';
import { PriceTag } from '@/components/product/PriceTag';
import { StockBadge } from '@/components/product/StockBadge';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';
import { extractErrorMessage } from '@/api/client';

export function ProductDetailPage() {
  const { productId } = useParams();
  const { data: product, isLoading, error } = useProduct(productId);
  usePageTitle(product?.name);
  const [activeImage, setActiveImage] = useState(0);
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const addToCart = useAddCartItem();

  if (isLoading) {
    return <ProductDetailSkeleton />;
  }

  if (error) {
    return (
      <div className="container py-12">
        <div className="rounded-md border border-destructive/30 bg-destructive/5 p-6 text-center text-sm text-destructive">
          {extractErrorMessage(error)}
        </div>
      </div>
    );
  }

  if (!product) return null;

  const images = (product.images ?? []).map((img) => img.url).filter(Boolean);
  const breadcrumbSegments = [
    ...(product.categoryPath ?? []).map((entry) => ({
      label: entry.name,
      to: `/c/${entry.slug}`,
    })),
    { label: product.name },
  ];
  const outOfStock = product.stockStatus === 'OUT_OF_STOCK';

  const handleAddToCart = () => {
    if (!isAuthenticated) {
      navigate(`/login?returnTo=${encodeURIComponent(location.pathname)}`);
      return;
    }
    addToCart.mutate({ productId: Number(productId), quantity: 1 });
  };

  return (
    <div className="container py-6">
      <Breadcrumb segments={breadcrumbSegments} />

      <div className="mt-6 grid gap-8 lg:grid-cols-[minmax(0,1fr)_400px]">
        <div className="space-y-3">
          <div className="overflow-hidden rounded-lg border bg-muted">
            <div className="relative aspect-square">
              {images[activeImage] ? (
                <img
                  src={images[activeImage]}
                  alt={product.name}
                  className="h-full w-full object-contain"
                />
              ) : (
                <div className="flex h-full w-full items-center justify-center text-muted-foreground">
                  <ImageOff className="h-16 w-16" />
                </div>
              )}
            </div>
          </div>
          {images.length > 1 && (
            <div className="flex gap-2 overflow-x-auto">
              {images.map((src, i) => (
                <button
                  key={src + i}
                  type="button"
                  onClick={() => setActiveImage(i)}
                  className={cn(
                    'h-20 w-20 shrink-0 overflow-hidden rounded-md border-2 bg-muted transition-colors',
                    activeImage === i ? 'border-primary' : 'border-transparent hover:border-border',
                  )}
                  aria-label={`View image ${i + 1}`}
                >
                  <img src={src} alt="" className="h-full w-full object-cover" />
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="space-y-5">
          <div>
            {product.brand && (
              <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                {product.brand}
              </p>
            )}
            <h1 className="mt-1 text-2xl font-bold tracking-tight">{product.name}</h1>
          </div>

          <div className="flex items-center gap-3">
            <PriceTag money={product.listPrice} size="lg" />
            <StockBadge
              status={product.stockStatus}
              unavailable={product.stockUnavailable}
            />
          </div>

          <Button
            size="lg"
            className="w-full"
            disabled={outOfStock || addToCart.isPending}
            onClick={handleAddToCart}
          >
            <ShoppingCart className="mr-2 h-5 w-5" />
            {outOfStock
              ? 'Out of stock'
              : addToCart.isPending
                ? 'Adding...'
                : 'Add to cart'}
          </Button>

          {product.description && (
            <div className="rounded-lg border bg-card p-5">
              <h2 className="mb-2 text-sm font-semibold">Description</h2>
              <p className="whitespace-pre-line text-sm leading-relaxed text-muted-foreground">
                {product.description}
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function ProductDetailSkeleton() {
  return (
    <div className="container py-6">
      <Skeleton className="h-4 w-48" />
      <div className="mt-6 grid gap-8 lg:grid-cols-[minmax(0,1fr)_400px]">
        <Skeleton className="aspect-square w-full" />
        <div className="space-y-4">
          <Skeleton className="h-3 w-20" />
          <Skeleton className="h-7 w-3/4" />
          <Skeleton className="h-8 w-32" />
          <Skeleton className="h-12 w-full" />
          <Skeleton className="h-32 w-full" />
        </div>
      </div>
    </div>
  );
}
