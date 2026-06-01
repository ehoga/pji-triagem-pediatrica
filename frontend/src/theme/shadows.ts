import { Platform } from 'react-native';

export const shadows = {
  card: Platform.select({
    ios: {
      shadowColor: '#0F2440',
      shadowOffset: { width: 0, height: 12 },
      shadowOpacity: 0.08,
      shadowRadius: 28,
    },
    android: {
      elevation: 3,
      shadowColor: '#0F2440',
    },
    default: {
      boxShadow: '0 12px 28px rgba(15, 36, 64, 0.08)',
    },
  }),
  lg: Platform.select({
    ios: {
      shadowColor: '#0F2440',
      shadowOffset: { width: 0, height: 20 },
      shadowOpacity: 0.14,
      shadowRadius: 48,
    },
    android: {
      elevation: 6,
      shadowColor: '#0F2440',
    },
    default: {
      boxShadow: '0 20px 48px rgba(15, 36, 64, 0.14)',
    },
  }),
};
