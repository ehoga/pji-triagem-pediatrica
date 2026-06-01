import type { ReactNode } from 'react';
import { Pressable, Text, View } from 'react-native';
import type { GestureResponderEvent } from 'react-native';
import { colors, radii, shadows, spacing, typography } from '../theme';
import { Icon } from './Icon';

interface ScreenHeaderProps {
  onBack?: (event: GestureResponderEvent) => void;
  right?: ReactNode;
  subtitle?: string;
  title: string;
}

export function ScreenHeader({ title, subtitle, onBack, right }: ScreenHeaderProps) {
  return (
    <View
      style={{
        alignItems: 'center',
        flexDirection: 'row',
        gap: spacing.xs,
        paddingHorizontal: spacing.lg,
        paddingVertical: spacing.sm,
      }}
    >
      <View style={{ width: 40 }}>
        {onBack ? (
          <Pressable
            accessibilityRole="button"
            accessibilityLabel="Voltar"
            onPress={onBack}
            style={({ pressed }) => [
              {
                alignItems: 'center',
                backgroundColor: colors.surface,
                borderRadius: radii.md,
                height: 38,
                justifyContent: 'center',
                opacity: pressed ? 0.7 : 1,
                width: 38,
              },
              shadows.card,
            ]}
          >
            <Icon name="back" size={18} />
          </Pressable>
        ) : null}
      </View>
      <View style={{ alignItems: 'center', flex: 1 }}>
        <Text selectable style={[typography.subtitle, { color: colors.text, textAlign: 'center' }]}>
          {title}
        </Text>
        {subtitle ? (
          <Text selectable style={[typography.caption, { color: colors.textMuted, marginTop: spacing.xxs / 2 }]}>
            {subtitle}
          </Text>
        ) : null}
      </View>
      <View style={{ alignItems: 'flex-end', minWidth: 40 }}>{right}</View>
    </View>
  );
}
