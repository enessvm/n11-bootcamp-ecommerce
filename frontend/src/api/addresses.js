import { api } from './client';

export async function getAddresses() {
  const { data } = await api.get('/users/me/addresses');
  return data;
}

export async function createAddress(payload) {
  const { data } = await api.post('/users/me/addresses', payload);
  return data;
}

export async function updateAddress(id, payload) {
  const { data } = await api.put(`/users/me/addresses/${id}`, payload);
  return data;
}

export async function deleteAddress(id) {
  await api.delete(`/users/me/addresses/${id}`);
}
