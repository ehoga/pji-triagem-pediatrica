import { Text, TextInput, View } from 'react-native';
import type { StyleProp, TextInputProps, TextStyle, ViewStyle } from 'react-native';
import { colors, radii, spacing, typography } from '../theme';

interface TextFieldProps extends TextInputProps {
  error?: string;
  hint?: string;
  inputStyle?: StyleProp<TextStyle>;
  label: string;
  style?: StyleProp<ViewStyle>;
}

export function TextField({ label, hint, error, style, inputStyle, ...props }: TextFieldProps) {
  return (
    <View style={[{ gap: spacing.xs }, style]}>
      <Text selectable style={[typography.caption, { color: colors.textMuted }]}>
        {label}
      </Text>
      <TextInput
        placeholderTextColor={colors.textSubtle}
        style={[
          {
            backgroundColor: colors.surface,
            borderColor: error ? colors.highSolid : colors.hairline,
            borderRadius: radii.lg,
            borderWidth: 1.2,
            color: colors.text,
            fontFamily: typography.body.fontFamily,
            fontSize: typography.body.fontSize,
            minHeight: 50,
            paddingHorizontal: spacing.md,
            paddingVertical: spacing.sm,
          },
          inputStyle,
        ]}
        {...props}
      />
      {error ? (
        <Text selectable style={[typography.caption, { color: colors.dangerText }]}>
          {error}
        </Text>
      ) : hint ? (
        <Text selectable style={[typography.caption, { color: colors.textSubtle }]}>
          {hint}
        </Text>
      ) : null}
    </View>
  );
}
