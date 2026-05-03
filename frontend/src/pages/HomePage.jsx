import { Link } from 'react-router-dom';
import { useCategories } from '@/hooks/useCategories';
import { useInfiniteProducts } from '@/hooks/useInfiniteProducts';
import { usePageTitle } from '@/hooks/usePageTitle';
import { ProductGrid } from '@/components/product/ProductGrid';
import { InfiniteScrollSentinel } from '@/components/InfiniteScrollSentinel';

export function HomePage() {
  usePageTitle('Home');
  const { data: categoriesData, isLoading: categoriesLoading } = useCategories();
  const categories = categoriesData?.categories ?? categoriesData ?? [];

  const {
    data,
    error,
    isLoading,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useInfiniteProducts({ sort: 'createdAt,desc' }, 10);

  const products = data?.pages.flatMap((p) => p.content) ?? [];
  const stockUnavailable = data?.pages?.[0]?.stockUnavailable ?? false;

  return (
    <div className="space-y-12 py-8">
      <section className="container">
        <div className="rounded-2xl bg-gradient-to-r from-primary to-primary/70 p-10 text-primary-foreground">
          <h1 className="text-3xl font-bold sm:text-4xl">Welcome to ECOM</h1>
          <p className="mt-2 max-w-xl text-primary-foreground/90">
            Browse the latest arrivals across every category.
          </p>
        </div>
      </section>

      {!categoriesLoading && categories.length > 0 && (
        <section className="container">
          <h2 className="mb-4 text-xl font-bold tracking-tight">Shop by category</h2>
          <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6">
            {categories.slice(0, 12).map((category) => (
              <Link
                key={category.id}
                to={`/c/${category.slug}`}
                className="rounded-lg border bg-card p-4 text-center text-sm font-medium transition-colors hover:border-primary hover:text-primary"
              >
                {category.name}
              </Link>
            ))}
          </div>
        </section>
      )}

      <section className="container">
        <div className="mb-4 flex items-baseline justify-between">
          <h2 className="text-xl font-bold tracking-tight">All products</h2>
        </div>

        <ProductGrid
          products={products}
          isLoading={isLoading}
          error={error}
          stockUnavailable={stockUnavailable}
          emptyMessage="No products yet. Check back soon."
        />

        <InfiniteScrollSentinel
          onLoadMore={fetchNextPage}
          hasMore={!!hasNextPage}
          isLoading={isFetchingNextPage}
        />

        {isFetchingNextPage && (
          <p className="py-6 text-center text-sm text-muted-foreground">
            Loading more...
          </p>
        )}

        {!isLoading && !hasNextPage && products.length > 0 && (
          <p className="py-6 text-center text-xs text-muted-foreground">
            You've reached the end.
          </p>
        )}
      </section>
    </div>
  );
}
