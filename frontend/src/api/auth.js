import axios from 'axios';
import { api } from './client';

const baseURL = import.meta.env.VITE_API_BASE_URL;

export async function register(payload) {
  const { data } = await axios.post(`${baseURL}/users/register`, payload);
  return data;
}

export async function login(credentials) {
  const { data } = await axios.post(`${baseURL}/users/login`, credentials);
  return data;
}

export async function refresh(refreshToken) {
  const { data } = await axios.post(`${baseURL}/users/refresh`, { refreshToken });
  return data;
}

export async function logout(refreshToken) {
  await api.post('/users/logout', { refreshToken });
}
