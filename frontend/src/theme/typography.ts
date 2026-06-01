import type { TextStyle } from 'react-native';

export const fontFamily = {
  regular: 'Nunito_400Regular',
  semibold: 'Nunito_600SemiBold',
  bold: 'Nunito_700Bold',
  extrabold: 'Nunito_800ExtraBold',
  black: 'Nunito_900Black',
};

export const typography = {
  title: {
    fontFamily: fontFamily.extrabold,
    fontSize: 22,
    lineHeight: 27,
  },
  subtitle: {
    fontFamily: fontFamily.bold,
    fontSize: 15,
    lineHeight: 20,
  },
  body: {
    fontFamily: fontFamily.semibold,
    fontSize: 13,
    lineHeight: 19,
  },
  bodyStrong: {
    fontFamily: fontFamily.extrabold,
    fontSize: 13,
    lineHeight: 18,
  },
  caption: {
    fontFamily: fontFamily.bold,
    fontSize: 11,
    lineHeight: 15,
  },
  eyebrow: {
    fontFamily: fontFamily.extrabold,
    fontSize: 11,
    lineHeight: 14,
    textTransform: 'uppercase',
    letterSpacing: 0.6,
  },
} satisfies Record<string, TextStyle>;
