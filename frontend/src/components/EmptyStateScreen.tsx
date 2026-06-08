import { ScrollView, Text, View } from 'react-native';
import { Icon, ScreenHeader } from './index';
import { colors, spacing, typography } from '../theme';
import type { IconName } from '../types/domain';

interface EmptyStateScreenProps {
  icon: IconName;
  onBack: () => void;
  subtitle?: string;
  title: string;
}

export function EmptyStateScreen({ icon, onBack, title, subtitle }: EmptyStateScreenProps) {
  return (
    <ScrollView
      contentContainerStyle={{ flexGrow: 1, justifyContent: 'center', paddingBottom: spacing.xxl }}
      contentInsetAdjustmentBehavior="automatic"
    >
      <ScreenHeader title={title} onBack={onBack} />
      <View style={{ alignItems: 'center', flex: 1, gap: spacing.md, justifyContent: 'center', paddingHorizontal: spacing.lg }}>
        <Icon name={icon} color={colors.primary} size={52} />
        <Text selectable={false} style={[typography.subtitle, { color: colors.text, textAlign: 'center' }]}>
          Em desenvolvimento
        </Text>
        {subtitle ? (
          <Text selectable style={[typography.body, { color: colors.textMuted, textAlign: 'center' }]}>
            {subtitle}
          </Text>
        ) : null}
      </View>
    </ScrollView>
  );
}
