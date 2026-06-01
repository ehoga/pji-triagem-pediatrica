import type { GestureResponderEvent } from 'react-native';
import { Text, View } from 'react-native';
import { colors, radii, riskPalette, spacing, typography } from '../theme';
import type { HistoryItem } from '../types/domain';
import { Card } from './Card';
import { Icon } from './Icon';

interface HistoryRowProps {
  item: HistoryItem;
  onPress?: (event: GestureResponderEvent) => void;
}

export function HistoryRow({ item, onPress }: HistoryRowProps) {
  const palette = riskPalette[item.risk];

  return (
    <Card
      onPress={onPress}
      padding={spacing.sm}
      contentStyle={{ alignItems: 'center', flexDirection: 'row', gap: spacing.sm }}
    >
      <View
        style={{
          alignItems: 'center',
          backgroundColor: palette.softer,
          borderRadius: radii.md,
          height: 42,
          justifyContent: 'center',
          width: 42,
        }}
      >
        <View style={{ backgroundColor: palette.solid, borderRadius: radii.pill, height: 10, width: 10 }} />
      </View>
      <View style={{ flex: 1 }}>
        <Text selectable style={[typography.bodyStrong, { color: colors.text }]}>
          {item.child} · {item.symptom}
        </Text>
        <Text selectable style={[typography.caption, { color: colors.textMuted, marginTop: spacing.xxs / 2 }]}>
          {item.date} · {palette.label}
        </Text>
      </View>
      <Icon name="chevronRight" size={18} color={colors.textSubtle} />
    </Card>
  );
}
