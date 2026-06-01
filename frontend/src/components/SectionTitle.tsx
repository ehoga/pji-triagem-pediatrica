import type { ReactNode } from 'react';
import { Text, View } from 'react-native';
import { colors, spacing, typography } from '../theme';

interface SectionTitleProps {
  action?: ReactNode;
  title: string;
}

export function SectionTitle({ title, action }: SectionTitleProps) {
  return (
    <View style={{ alignItems: 'baseline', flexDirection: 'row', justifyContent: 'space-between', marginBottom: spacing.xs }}>
      <Text selectable style={[typography.eyebrow, { color: colors.textMuted }]}>
        {title}
      </Text>
      {action}
    </View>
  );
}
