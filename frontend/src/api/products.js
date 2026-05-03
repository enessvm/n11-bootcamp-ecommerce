import { api } from './client';

export async function getProducts(params = {}) {
  const { data } = await api.get('/products', { params });
  return data;
}

export async function getProductById(id) {
  const { data } = await api.get(`/products/${id}`);
  return data;
}
