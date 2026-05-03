import { api } from './client';

export async function validatePromotion(payload) {
  const { data } = await api.post('/promotions/validate', payload);
  return data;
}
