import { api } from './client';

export async function getCart() {
  const { data } = await api.get('/cart');
  return data;
}

export async function addCartItem(payload) {
  const { data } = await api.post('/cart/items', payload);
  return data;
}

export async function updateCartItem(productId, payload) {
  const { data } = await api.put(`/cart/items/${productId}`, payload);
  return data;
}

export async function removeCartItem(productId) {
  const { data } = await api.delete(`/cart/items/${productId}`);
  return data;
}

export async function clearCart() {
  const { data } = await api.delete('/cart');
  return data;
}
