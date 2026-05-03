import { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  ChevronLeft,
  ChevronRight,
  ImageOff,
  Package,
} from 'lucide-react';
import { useMyOrders } from '@/hooks/useMyOrders';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { PriceTag } from '@/components/product/PriceTag';
import { extractErrorMessage } from '@/api/client';

const PAGE_SIZE = 10;

export function AccountOrdersPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading, error, isFetching } = useMyOrders({
    page,
    size: PAGE_SIZE,
  });

  if (isLoading) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-24" />
        <Skeleton className="h-24" />
        <Skeleton className="h-24" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-md border border-destructive/30 bg-destructive/5 p-6 text-center text-sm text-destructive">
        {extractErrorMessage(error)}
      </div>
    );
  }

  const orders = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;

  if (orders.length === 0) {
    return (
      <div className="rounded-lg border bg-card p-12 text-center">
        <Package className="mx-auto h-10 w-10 text-muted-foreground" />
        <p className="mt-3 text-sm font-medium text-foreground">
          No orders yet
        </p>
        <p className="mt-1 text-sm text-muted-foreground">
          Browse the storefront and place your first order.
        </p>
        <Button asChild className="mt-4">
          <Link to="/">Continue shopping</Link>
        </Button>
      </div>
    );
  }

  const onPageChange = (newPage) => {
    setPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="space-y-4">
      <div className="flex items-baseline justify-between">
        <h2 className="text-lg font-semibold">Orders</h2>
        <p className="text-sm text-muted-foreground">
          {totalElements} {totalElements === 1 ? 'order' : 'orders'}
        </p>
      </div>

      <div className="space-y-3">
        {orders.map((order) => (
          <OrderRow key={order.id} order={order} />
        ))}
      </div>

      {totalPages > 1 && (
        <div className="flex items-center justify-between border-t pt-4">
          <p className="text-sm text-muted-foreground">
            Page {page + 1} of {totalPages}
            {isFetching && ' • updating...'}
          </p>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              disabled={page === 0}
              onClick={() => onPageChange(page - 1)}
            >
              <ChevronLeft className="mr-1 h-4 w-4" />
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              disabled={page >= totalPages - 1}
              onClick={() => onPageChange(page + 1)}
            >
              Next
              <ChevronRight className="ml-1 h-4 w-4" />
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}

function OrderRow({ order }) {
  const otherCount = Math.max(order.itemCount - 1, 0);
  const summary = otherCount === 0
    ? order.firstItemName
    : `${order.firstItemName} + ${otherCount} ${otherCount === 1 ? 'item' : 'items'}`;

  return (
    <Link
      to={`/orders/${order.id}`}
      className="flex gap-4 rounded-lg border bg-card p-4 transition-colors hover:border-primary"
    >
      <div className="h-16 w-16 shrink-0 overflow-hidden rounded border bg-muted">
        {order.firstItemImageUrl ? (
          <img
            src={order.firstItemImageUrl}
            alt=""
            className="h-full w-full object-cover"
            loading="lazy"
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center text-muted-foreground">
            <ImageOff className="h-5 w-5" />
          </div>
        )}
      </div>

      <div className="flex flex-1 flex-col justify-between gap-1 sm:flex-row sm:items-center">
        <div className="min-w-0">
          <p className="text-xs uppercase tracking-wide text-muted-foreground">
            {formatDate(order.createdAt)}
          </p>
          {summary && (
            <p className="mt-1 line-clamp-1 text-sm font-medium text-foreground">
              {summary}
            </p>
          )}
        </div>
        <div className="text-right">
          <PriceTag money={order.total} size="md" />
        </div>
      </div>
    </Link>
  );
}

const DATE_FMT = new Intl.DateTimeFormat('tr-TR', {
  year: 'numeric',
  month: 'short',
  day: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
});

function formatDate(iso) {
  if (!iso) return '';
  try {
    return DATE_FMT.format(new Date(iso));
  } catch {
    return iso;
  }
}
