import { api } from './api';
import { getToken, getUser } from './storage';
import { demoCredentials } from '../mocks/appMock';
import { mockLogin, mockMe, mockRegister } from '../mocks/authMock';
import type { AuthSession, AuthUser, LoginCredentials, RegisterPayload } from '../types/auth';

const useMockAuth = process.env.EXPO_PUBLIC_USE_MOCK_AUTH !== 'false';

export async function loginRequest(credentials: LoginCredentials): Promise<AuthSession> {
  if (useMockAuth) {
    return mockLogin(credentials);
  }

  const response = await api.post<AuthSession>('/auth/login', credentials);
  return response.data;
}

export async function registerRequest(payload: RegisterPayload): Promise<AuthSession> {
  if (useMockAuth) {
    return mockRegister(payload);
  }

  const response = await api.post<AuthSession>('/auth/register', payload);
  return response.data;
}

export async function meRequest(): Promise<AuthUser> {
  const token = await getToken();
  const storedUser = await getUser();

  if (useMockAuth) {
    return mockMe(token, storedUser);
  }

  const response = await api.get<AuthUser>('/auth/me');
  return response.data;
}

export function getDemoCredentials(): LoginCredentials {
  return demoCredentials;
}
