import axios from 'axios';
import { clearAuth, getToken } from './storage';

export const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080';

type UnauthorizedHandler = () => void | Promise<void>;

let unauthorizedHandler: UnauthorizedHandler | null = null;

export function setUnauthorizedHandler(handler: UnauthorizedHandler | null): void {
  unauthorizedHandler = handler;
}

export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

api.interceptors.request.use(async (config) => {
  const token = await getToken();
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error?.response?.status === 401) {
      await clearAuth();
      if (unauthorizedHandler) await unauthorizedHandler();
    }

    return Promise.reject(error);
  },
);

export async function checkHealth(): Promise<unknown> {
  const response = await api.get<unknown>('/health');
  return response.data;
}
