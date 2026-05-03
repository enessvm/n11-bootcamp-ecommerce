import { api } from './client';

export async function createOrder(payload) {
  const { data } = await api.post('/orders', payload);
  return data;
}

export async function getOrder(id) {
  const { data } = await api.get(`/orders/${id}`);
  return data;
}

export async function listMyOrders(params = {}) {
  const { data } = await api.get('/orders', { params });
  return data;
}
