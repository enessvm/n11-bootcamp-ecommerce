import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { useAuth } from '@/auth/useAuth';
import { extractErrorMessage } from '@/api/client';
import * as addressesApi from '@/api/addresses';

const ADDRESSES_KEY = ['addresses'];

export function useAddresses() {
  const { isAuthenticated } = useAuth();
  return useQuery({
    queryKey: ADDRESSES_KEY,
    queryFn: addressesApi.getAddresses,
    enabled: isAuthenticated,
    staleTime: 60 * 1000,
  });
}

export function useCreateAddress() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: addressesApi.createAddress,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ADDRESSES_KEY });
      toast.success('Address saved');
    },
    onError: (err) => toast.error(extractErrorMessage(err)),
  });
}

export function useUpdateAddress() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }) => addressesApi.updateAddress(id, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ADDRESSES_KEY });
      toast.success('Address updated');
    },
    onError: (err) => toast.error(extractErrorMessage(err)),
  });
}

export function useDeleteAddress() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: addressesApi.deleteAddress,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ADDRESSES_KEY });
      toast.success('Address deleted');
    },
    onError: (err) => toast.error(extractErrorMessage(err)),
  });
}
