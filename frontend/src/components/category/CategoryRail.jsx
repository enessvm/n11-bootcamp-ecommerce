import { NavLink } from 'react-router-dom';
import { useCategories } from '@/hooks/useCategories';
import { cn } from '@/lib/utils';

export function CategoryRail() {
  const { data, isLoading } = useCategories();
  const categories = data?.categories ?? data ?? [];

  if (isLoading) {
    return (
      <div className="border-b bg-background">
        <div className="container flex h-11 items-center gap-3 overflow-x-auto">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="h-5 w-20 animate-pulse rounded bg-muted" />
          ))}
        </div>
      </div>
    );
  }

  if (categories.length === 0) return null;

  return (
    <nav aria-label="Categories" className="border-b bg-background">
      <div className="container flex h-11 items-center gap-1 overflow-x-auto">
        {categories.map((category) => (
          <NavLink
            key={category.id}
            to={`/c/${category.slug}`}
            className={({ isActive }) =>
              cn(
                'inline-flex h-8 shrink-0 items-center rounded-md px-3 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-primary/10 text-primary'
                  : 'text-foreground/80 hover:bg-accent hover:text-foreground',
              )
            }
          >
            {category.name}
          </NavLink>
        ))}
      </div>
    </nav>
  );
}
