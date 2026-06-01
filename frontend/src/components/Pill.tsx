import type { ReactNode } from 'react';
import { Text, View } from 'react-native';
import type { StyleProp, TextStyle, ViewStyle } from 'react-native';
import { colors, radii, riskPalette, spacing, typography } from '../theme';
import type { RiskTone } from '../types/domain';

const tones = {
  primary: { backgroundColor: colors.primarySoft, color: colors.primaryDark },
  low: { backgroundColor: colors.lowSoft, color: colors.successText },
  mod: { backgroundColor: colors.modSoft, color: colors.warningText },
  high: { backgroundColor: colors.highSoft, color: colors.dangerText },
  neutral: { backgroundColor: colors.neutralSoft, color: colors.navy },
} satisfies Record<RiskTone, { backgroundColor: string; color: string }>;

interface PillProps {
  children: ReactNode;
  style?: StyleProp<ViewStyle>;
  textStyle?: StyleProp<TextStyle>;
  tone?: RiskTone;
  withDot?: boolean;
}

export function Pill({ tone = 'neutral', children, withDot = false, style, textStyle }: PillProps) {
  const palette = tones[tone];
  const dotColor = tone in riskPalette ? riskPalette[tone as keyof typeof riskPalette].solid : palette.color;

  return (
    <View
      style={[
        {
          alignSelf: 'flex-start',
          alignItems: 'center',
          backgroundColor: palette.backgroundColor,
          borderRadius: radii.pill,
          flexDirection: 'row',
          gap: spacing.xs,
          paddingHorizontal: spacing.sm,
          paddingVertical: spacing.xxs,
        },
        style,
      ]}
    >
      {withDot ? (
        <View style={{ width: 6, height: 6, borderRadius: radii.pill, backgroundColor: dotColor }} />
      ) : null}
      <Text selectable style={[typography.caption, { color: palette.color }, textStyle]}>
        {children}
      </Text>
    </View>
  );
}
