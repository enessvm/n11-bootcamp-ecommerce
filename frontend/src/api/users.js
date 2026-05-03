import { api } from './client';

export async function getMe() {
  const { data } = await api.get('/users/me');
  return data;
}

export async function updateMe(payload) {
  const { data } = await api.put('/users/me', payload);
  return data;
}
