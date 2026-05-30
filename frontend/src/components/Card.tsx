import type { ReactNode } from 'react';
import { Pressable, View } from 'react-native';
import type { GestureResponderEvent, StyleProp, ViewStyle } from 'react-native';
import { colors, radii, shadows, spacing } from '../theme';

interface CardProps {
  children: ReactNode;
  contentStyle?: StyleProp<ViewStyle>;
  onPress?: (event: GestureResponderEvent) => void;
  padding?: number;
  style?: StyleProp<ViewStyle>;
}

export function Card({ children, onPress, padding = spacing.md, style, contentStyle }: CardProps) {
  const baseStyle = [
    {
      backgroundColor: colors.surface,
      borderRadius: radii.card,
      borderWidth: 1,
      borderColor: colors.hairline,
      padding,
    },
    shadows.card,
    style,
  ];

  if (onPress) {
    return (
      <Pressable
        accessibilityRole="button"
        onPress={onPress}
        style={({ pressed }) => [baseStyle, pressed && { opacity: 0.86, transform: [{ scale: 0.99 }] }]}
      >
        <View style={contentStyle}>{children}</View>
      </Pressable>
    );
  }

  return (
    <View style={baseStyle}>
      <View style={contentStyle}>{children}</View>
    </View>
  );
}
