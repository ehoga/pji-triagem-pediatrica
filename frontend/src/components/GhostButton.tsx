import type { ReactNode } from 'react';
import { Pressable, Text } from 'react-native';
import type { GestureResponderEvent, StyleProp, ViewStyle } from 'react-native';
import { colors, radii, spacing, typography } from '../theme';

interface GhostButtonProps {
  children: ReactNode;
  disabled?: boolean;
  full?: boolean;
  icon?: ReactNode;
  onPress?: (event: GestureResponderEvent) => void;
  style?: StyleProp<ViewStyle>;
}

export function GhostButton({ children, onPress, icon, full = true, disabled = false, style }: GhostButtonProps) {
  return (
    <Pressable
      accessibilityRole="button"
      disabled={disabled}
      onPress={onPress}
      style={({ pressed }) => [
        {
          alignItems: 'center',
          alignSelf: full ? 'stretch' : 'flex-start',
          backgroundColor: colors.surface,
          borderColor: colors.hairline,
          borderRadius: radii.lg,
          borderWidth: 1.5,
          flexDirection: 'row',
          gap: spacing.xs,
          justifyContent: 'center',
          minHeight: 48,
          opacity: disabled ? 0.5 : pressed ? 0.78 : 1,
          paddingHorizontal: spacing.md,
          paddingVertical: spacing.sm,
        },
        style,
      ]}
    >
      {icon}
      <Text selectable={false} style={[typography.bodyStrong, { color: colors.navy }]}>
        {children}
      </Text>
    </Pressable>
  );
}
