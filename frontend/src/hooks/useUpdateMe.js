import { useMutation } from '@tanstack/react-query';
import { toast } from 'sonner';
import { updateMe } from '@/api/users';
import { useAuth } from '@/auth/useAuth';
import { extractErrorMessage } from '@/api/client';

export function useUpdateMe() {
  const { setUser } = useAuth();
  return useMutation({
    mutationFn: updateMe,
    onSuccess: (profile) => {
      setUser(profile);
      toast.success('Profile updated');
    },
    onError: (err) => toast.error(extractErrorMessage(err)),
  });
}
