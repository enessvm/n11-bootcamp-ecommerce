import { useInfiniteQuery } from '@tanstack/react-query';
import { getProducts } from '@/api/products';

export function useInfiniteProducts(baseParams = {}, pageSize = 10) {
  return useInfiniteQuery({
    queryKey: ['products', 'infinite', baseParams, pageSize],
    queryFn: ({ pageParam = 0 }) =>
      getProducts({ ...baseParams, page: pageParam, size: pageSize }),
    initialPageParam: 0,
    getNextPageParam: (lastPage) =>
      lastPage.last ? undefined : lastPage.page + 1,
    staleTime: 60 * 1000,
  });
}
