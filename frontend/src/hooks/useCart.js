import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import {
  addCartItem,
  clearCart,
  getCart,
  removeCartItem,
  updateCartItem,
} from '@/api/cart';
import { useAuth } from '@/auth/useAuth';
import { extractErrorMessage } from '@/api/client';

const CART_KEY = ['cart'];

export function useCart() {
  const { isAuthenticated } = useAuth();
  return useQuery({
    queryKey: CART_KEY,
    queryFn: getCart,
    enabled: isAuthenticated,
    staleTime: 30 * 1000,
  });
}

export function useCartItemCount() {
  const { data } = useCart();
  if (!data?.items) return 0;
  return data.items.reduce((sum, item) => sum + (item.quantity ?? 0), 0);
}

export function useAddCartItem() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ productId, quantity = 1 }) =>
      addCartItem({ productId, quantity }),
    onSuccess: (cart) => {
      queryClient.setQueryData(CART_KEY, cart);
      toast.success('Added to cart');
    },
    onError: (err) => toast.error(extractErrorMessage(err)),
  });
}

export function useUpdateCartItem() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ productId, quantity }) =>
      updateCartItem(productId, { quantity }),
    onSuccess: (cart) => queryClient.setQueryData(CART_KEY, cart),
    onError: (err) => toast.error(extractErrorMessage(err)),
  });
}

export function useRemoveCartItem() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ productId }) => removeCartItem(productId),
    onSuccess: (cart) => queryClient.setQueryData(CART_KEY, cart),
    onError: (err) => toast.error(extractErrorMessage(err)),
  });
}

export function useClearCart() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: clearCart,
    onSuccess: (cart) => {
      queryClient.setQueryData(CART_KEY, cart);
      toast.success('Cart cleared');
    },
    onError: (err) => toast.error(extractErrorMessage(err)),
  });
}
