import axios from 'axios';
import { tokenStorage } from '@/auth/tokenStorage';

const baseURL = import.meta.env.VITE_API_BASE_URL;

export const api = axios.create({ baseURL });

api.interceptors.request.use((config) => {
  const { accessToken } = tokenStorage.read();
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

let onSessionLost = null;

export function setOnSessionLost(handler) {
  onSessionLost = handler;
}

let refreshInFlight = null;
const queue = [];

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;

    if (status !== 401 || originalRequest?._retry || originalRequest?.url?.includes('/users/refresh')) {
      return Promise.reject(error);
    }

    const stored = tokenStorage.read();
    if (!stored.refreshToken) {
      if (onSessionLost) onSessionLost();
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    if (!refreshInFlight) {
      refreshInFlight = axios
        .post(`${baseURL}/users/refresh`, { refreshToken: stored.refreshToken })
        .then((res) => {
          tokenStorage.write({
            accessToken: res.data.accessToken,
            refreshToken: res.data.refreshToken,
          });
          return res.data.accessToken;
        })
        .catch((err) => {
          tokenStorage.clear();
          if (onSessionLost) onSessionLost();
          throw err;
        })
        .finally(() => {
          refreshInFlight = null;
        });
    }

    try {
      const newAccessToken = await refreshInFlight;
      originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
      return api(originalRequest);
    } catch (refreshErr) {
      return Promise.reject(refreshErr);
    }
  },
);

const STATUS_FALLBACKS = {
  400: 'The request was invalid. Please check your input and try again.',
  401: 'Authentication failed. Please sign in again.',
  403: "You don't have permission to do that.",
  404: 'We could not find what you were looking for.',
  409: 'That conflicts with something that already exists.',
  422: 'Some fields are not valid. Please review and try again.',
  429: 'Too many requests. Please wait a moment and try again.',
  500: 'Something went wrong on our end. Please try again shortly.',
  502: 'The server is unreachable right now. Please try again shortly.',
  503: 'The service is temporarily unavailable. Please try again shortly.',
  504: 'The server took too long to respond. Please try again.',
};

export function extractErrorMessage(error) {
  const data = error?.response?.data;
  const backendMessage =
    data && typeof data === 'object' && typeof data.message === 'string'
      ? data.message.trim()
      : '';
  if (backendMessage) {
    return backendMessage;
  }

  const status = error?.response?.status;
  if (status && STATUS_FALLBACKS[status]) {
    return STATUS_FALLBACKS[status];
  }
  if (typeof status === 'number' && status >= 500) {
    return STATUS_FALLBACKS[500];
  }
  if (typeof status === 'number' && status >= 400) {
    return STATUS_FALLBACKS[400];
  }

  if (error?.code === 'ERR_NETWORK' || !error?.response) {
    return 'Cannot reach the server. Please check your connection and try again.';
  }

  return 'Something went wrong. Please try again.';
}

export function extractErrorCode(error) {
  const data = error?.response?.data;
  if (data && typeof data === 'object' && typeof data.error === 'string') {
    return data.error;
  }
  return null;
}
