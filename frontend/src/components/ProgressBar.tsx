import { Text, View } from 'react-native';
import { colors, radii, spacing, typography } from '../theme';

interface ProgressBarProps {
  tone?: string;
  total: number;
  value: number;
}

export function ProgressBar({ value, total, tone = colors.primary }: ProgressBarProps) {
  const safeTotal = Math.max(total, 1);
  const ratio = Math.max(0, Math.min(1, value / safeTotal));

  return (
    <View style={{ paddingHorizontal: spacing.lg, paddingBottom: spacing.md, paddingTop: spacing.xxs }}>
      <View
        style={{
          backgroundColor: colors.neutralMuted,
          borderRadius: radii.pill,
          height: 6,
          overflow: 'hidden',
        }}
      >
        <View
          style={{
            backgroundColor: tone,
            borderRadius: radii.pill,
            height: '100%',
            width: `${ratio * 100}%`,
          }}
        />
      </View>
      <View style={{ flexDirection: 'row', justifyContent: 'space-between', marginTop: spacing.xs }}>
        <Text selectable style={[typography.caption, { color: colors.textMuted }]}>
          Etapa {value} de {safeTotal}
        </Text>
        <Text selectable style={[typography.caption, { color: colors.textMuted, fontVariant: ['tabular-nums'] }]}>
          {Math.round(ratio * 100)}%
        </Text>
      </View>
    </View>
  );
}
