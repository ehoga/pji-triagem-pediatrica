import type { GestureResponderEvent } from 'react-native';
import { Text, View } from 'react-native';
import { colors, radii, riskPalette, spacing, typography } from '../theme';
import type { RiskTone, Symptom } from '../types/domain';
import { Card } from './Card';
import { Icon } from './Icon';

const toneMap = {
  primary: { solid: colors.primary, softer: colors.primarySoft },
  neutral: { solid: colors.navy, softer: colors.neutralSoft },
  low: riskPalette.low,
  mod: riskPalette.mod,
  high: riskPalette.high,
} satisfies Record<RiskTone, { solid: string; softer: string }>;

interface SymptomCardProps {
  onPress?: (event: GestureResponderEvent) => void;
  symptom: Symptom;
}

export function SymptomCard({ symptom, onPress }: SymptomCardProps) {
  const tone = toneMap[symptom.tone];

  return (
    <Card
      onPress={onPress}
      padding={spacing.sm}
      contentStyle={{ alignItems: 'center', flexDirection: 'row', gap: spacing.sm }}
    >
      <View
        style={{
          alignItems: 'center',
          backgroundColor: tone.softer,
          borderRadius: radii.lg,
          height: 46,
          justifyContent: 'center',
          width: 46,
        }}
      >
        <Icon name={symptom.icon} color={tone.solid} size={22} />
      </View>
      <View style={{ flex: 1 }}>
        <Text selectable style={[typography.bodyStrong, { color: colors.text }]}>
          {symptom.name}
        </Text>
        <Text selectable style={[typography.caption, { color: colors.textMuted, marginTop: spacing.xxs / 2 }]}>
          {symptom.desc}
        </Text>
      </View>
      <Icon name="chevronRight" size={18} color={colors.textSubtle} />
    </Card>
  );
}
