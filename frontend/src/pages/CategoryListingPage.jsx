import { useMemo } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { useCategories } from '@/hooks/useCategories';
import { useInfiniteProducts } from '@/hooks/useInfiniteProducts';
import { usePageTitle } from '@/hooks/usePageTitle';
import { ProductGrid } from '@/components/product/ProductGrid';
import { Breadcrumb } from '@/components/category/Breadcrumb';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Input } from '@/components/ui/input';
import { InfiniteScrollSentinel } from '@/components/InfiniteScrollSentinel';
import { ArrowUpDown } from 'lucide-react';

const SORT_OPTIONS = [
  { value: 'createdAt,desc', label: 'Newest first' },
  { value: 'listPrice,asc', label: 'Price: low to high' },
  { value: 'listPrice,desc', label: 'Price: high to low' },
  { value: 'name,asc', label: 'Name: A to Z' },
];

const PAGE_SIZE = 10;

export function CategoryListingPage() {
  const { categorySlug } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();

  const sort = searchParams.get('sort') ?? 'createdAt,desc';
  const minPrice = searchParams.get('minPrice') ?? '';
  const maxPrice = searchParams.get('maxPrice') ?? '';

  // Resolve slug → id from the cached categories list (single fetch, reused).
  const { data: categoriesData } = useCategories();
  const categories = categoriesData?.categories ?? categoriesData ?? [];
  const category = categories.find((c) => c.slug === categorySlug);
  usePageTitle(category?.name);

  const baseParams = useMemo(() => {
    const params = { sort };
    if (category) params.categoryId = category.id;
    if (minPrice) params.minPrice = minPrice;
    if (maxPrice) params.maxPrice = maxPrice;
    return params;
  }, [sort, category, minPrice, maxPrice]);

  const {
    data,
    error,
    isLoading,
    isFetchingNextPage,
    fetchNextPage,
    hasNextPage,
  } = useInfiniteProducts(baseParams, PAGE_SIZE);

  const products = data?.pages.flatMap((p) => p.content) ?? [];
  const stockUnavailable = data?.pages?.[0]?.stockUnavailable ?? false;
  const totalElements = data?.pages?.[0]?.totalElements ?? 0;

  const updateParams = (updates) => {
    const next = new URLSearchParams(searchParams);
    for (const [k, v] of Object.entries(updates)) {
      if (v == null || v === '') next.delete(k);
      else next.set(k, String(v));
    }
    setSearchParams(next, { replace: false });
  };

  const onSortChange = (value) => updateParams({ sort: value });

  const handlePriceSubmit = (e) => {
    e.preventDefault();
    const fd = new FormData(e.currentTarget);
    updateParams({
      minPrice: fd.get('minPrice') || '',
      maxPrice: fd.get('maxPrice') || '',
    });
  };

  const sortLabel = SORT_OPTIONS.find((o) => o.value === sort)?.label ?? 'Sort';

  return (
    <div className="container py-6">
      <Breadcrumb segments={[{ label: category?.name ?? 'Category' }]} />

      <div className="mt-4 flex flex-wrap items-baseline justify-between gap-3">
        <h1 className="text-2xl font-bold tracking-tight">
          {category?.name ?? 'Products'}
        </h1>
        {totalElements > 0 && (
          <p className="text-sm text-muted-foreground">
            {totalElements} {totalElements === 1 ? 'product' : 'products'}
          </p>
        )}
      </div>

      <div className="mt-6 grid gap-6 lg:grid-cols-[240px_minmax(0,1fr)]">
        <aside className="space-y-6">
          <div className="rounded-lg border bg-card p-4">
            <h2 className="mb-3 text-sm font-semibold">Price</h2>
            <form onSubmit={handlePriceSubmit} className="space-y-2">
              <div className="flex gap-2">
                <Input
                  name="minPrice"
                  type="number"
                  inputMode="numeric"
                  min="0"
                  placeholder="Min"
                  defaultValue={minPrice}
                  className="h-9"
                />
                <Input
                  name="maxPrice"
                  type="number"
                  inputMode="numeric"
                  min="0"
                  placeholder="Max"
                  defaultValue={maxPrice}
                  className="h-9"
                />
              </div>
              <Button type="submit" size="sm" variant="secondary" className="w-full">
                Apply
              </Button>
              {(minPrice || maxPrice) && (
                <Button
                  type="button"
                  size="sm"
                  variant="ghost"
                  className="w-full"
                  onClick={() => updateParams({ minPrice: '', maxPrice: '' })}
                >
                  Clear
                </Button>
              )}
            </form>
          </div>
        </aside>

        <div className="space-y-4">
          <div className="flex items-center justify-end">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="outline" size="sm">
                  <ArrowUpDown className="mr-1.5 h-4 w-4" />
                  {sortLabel}
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                {SORT_OPTIONS.map((option) => (
                  <DropdownMenuItem
                    key={option.value}
                    onSelect={() => onSortChange(option.value)}
                  >
                    {option.label}
                  </DropdownMenuItem>
                ))}
              </DropdownMenuContent>
            </DropdownMenu>
          </div>

          <ProductGrid
            products={products}
            isLoading={isLoading}
            error={error}
            stockUnavailable={stockUnavailable}
            emptyMessage="No products match these filters."
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
        </div>
      </div>
    </div>
  );
}
