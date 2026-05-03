import { useMutation } from '@tanstack/react-query';
import { validatePromotion } from '@/api/promotions';

export function useValidatePromotion() {
  return useMutation({
    mutationFn: validatePromotion,
  });
}
