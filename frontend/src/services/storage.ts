import AsyncStorage from '@react-native-async-storage/async-storage';
import { STORAGE_KEYS } from './storageKeys';
import type { AuthUser } from '../types/auth';

async function getString(key: string): Promise<string | null> {
  try {
    return await AsyncStorage.getItem(key);
  } catch {
    return null;
  }
}

async function getJson<T>(key: string): Promise<T | null> {
  const value = await getString(key);
  if (!value) return null;

  try {
    return JSON.parse(value) as T;
  } catch {
    return null;
  }
}

async function setJson<T>(key: string, value: T): Promise<void> {
  try {
    await AsyncStorage.setItem(key, JSON.stringify(value));
  } catch {
    // Storage failures should not crash UI flows in Expo Go demos.
  }
}

export async function getToken(): Promise<string | null> {
  return getString(STORAGE_KEYS.token);
}

export async function setToken(token: string | null | undefined): Promise<void> {
  try {
    if (!token) {
      await AsyncStorage.removeItem(STORAGE_KEYS.token);
      return;
    }
    await AsyncStorage.setItem(STORAGE_KEYS.token, token);
  } catch {
    // Keep API identical to AsyncStorage helper acceptance: no throw on storage miss/failure.
  }
}

export async function getUser(): Promise<AuthUser | null> {
  return getJson<AuthUser>(STORAGE_KEYS.user);
}

export async function setUser(user: AuthUser | null | undefined): Promise<void> {
  if (!user) {
    try {
      await AsyncStorage.removeItem(STORAGE_KEYS.user);
    } catch {
      // no-op
    }
    return;
  }

  await setJson(STORAGE_KEYS.user, user);
}

export async function clearAuth(): Promise<void> {
  try {
    await AsyncStorage.multiRemove([STORAGE_KEYS.token, STORAGE_KEYS.user]);
  } catch {
    // no-op
  }
}

export async function getDisclaimerAcepto(): Promise<boolean | null> {
  const value = await getString(STORAGE_KEYS.disclaimerAccepted);
  if (value === null) return null;
  return value === 'true';
}

export async function setDisclaimerAcepto(accepted: boolean): Promise<void> {
  try {
    await AsyncStorage.setItem(STORAGE_KEYS.disclaimerAccepted, accepted ? 'true' : 'false');
  } catch {
    // no-op
  }
}

export const getDisclaimerAccepted = getDisclaimerAcepto;
export const setDisclaimerAccepted = setDisclaimerAcepto;
