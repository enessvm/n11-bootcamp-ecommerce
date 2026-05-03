import { useQuery } from '@tanstack/react-query';
import { getProductById } from '@/api/products';

export function useProduct(id) {
  return useQuery({
    queryKey: ['product', id],
    queryFn: () => getProductById(id),
    enabled: id != null,
    staleTime: 60 * 1000,
  });
}
