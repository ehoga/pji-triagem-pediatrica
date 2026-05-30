export const colors = {
  primary: '#2F80ED',
  primaryDark: '#1E66C9',
  secondary: '#56CCF2',
  navy: '#1E3A5F',

  background: '#F4F7FB',
  surface: '#FFFFFF',
  surfaceSoft: '#EEF4FE',
  hairline: 'rgba(30,58,95,0.08)',
  divider: 'rgba(30,58,95,0.06)',

  text: '#0F2440',
  textMuted: '#5B6B82',
  textSubtle: '#8C9AAE',
  inverseText: '#FFFFFF',
  inverseOverlay: 'rgba(255,255,255,0.18)',
  tabBar: 'rgba(255,255,255,0.96)',
  mascotCalmEnd: '#DDEBFD',

  primarySoft: '#E8F1FE',
  lowSolid: '#27AE60',
  lowSoft: '#DFF6EA',
  lowSofter: '#EFFAF3',
  modSolid: '#E2A52E',
  modSoft: '#FFF4CC',
  modSofter: '#FFFBE8',
  highSolid: '#EB5757',
  highSoft: '#FDE2E2',
  highSofter: '#FEF1F1',
  neutralSoft: '#EEF2F7',
  neutralMuted: '#D7E0EC',

  dangerText: '#A33333',
  warningText: '#7A5A0E',
  successText: '#1E7E47',
};

export const riskPalette = {
  low: {
    solid: colors.lowSolid,
    soft: colors.lowSoft,
    softer: colors.lowSofter,
    text: colors.successText,
    label: 'Baixo risco',
  },
  mod: {
    solid: colors.modSolid,
    soft: colors.modSoft,
    softer: colors.modSofter,
    text: colors.warningText,
    label: 'Risco moderado',
  },
  high: {
    solid: colors.highSolid,
    soft: colors.highSoft,
    softer: colors.highSofter,
    text: colors.dangerText,
    label: 'Alto risco',
  },
};
