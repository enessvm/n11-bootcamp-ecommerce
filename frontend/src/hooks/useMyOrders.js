import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { listMyOrders } from '@/api/orders';
import { useAuth } from '@/auth/useAuth';

export function useMyOrders({ page = 0, size = 10 } = {}) {
  const { isAuthenticated } = useAuth();
  return useQuery({
    queryKey: ['my-orders', page, size],
    queryFn: () => listMyOrders({ page, size }),
    enabled: isAuthenticated,
    placeholderData: keepPreviousData,
    staleTime: 30 * 1000,
  });
}
