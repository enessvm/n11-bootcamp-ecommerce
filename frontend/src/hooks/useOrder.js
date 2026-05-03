import { useQuery } from '@tanstack/react-query';
import { getOrder } from '@/api/orders';
import { isTerminalSagaState } from '@/lib/sagaState';

/**
 * Polls /orders/{id} every 2s while the saga is still in flight.
 * Stops polling once the saga reaches a terminal state.
 */
export function useOrder(id, { polling = false } = {}) {
  return useQuery({
    queryKey: ['order', id],
    queryFn: () => getOrder(id),
    enabled: id != null,
    refetchInterval: polling
      ? (query) => (isTerminalSagaState(query.state.data?.sagaState) ? false : 2000)
      : false,
    staleTime: 0,
  });
}
