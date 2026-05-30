import { Text, View } from 'react-native';
import type { GestureResponderEvent } from 'react-native';
import { colors, spacing, typography } from '../theme';
import { GhostButton } from './GhostButton';
import { Icon } from './Icon';
import { Mascot } from './Mascot';

interface LoadingStateProps {
  message?: string;
  title?: string;
}

interface EmptyStateProps {
  actionLabel?: string;
  message?: string;
  onAction?: (event: GestureResponderEvent) => void;
  title: string;
}

interface ErrorStateProps {
  message?: string;
  onRetry?: (event: GestureResponderEvent) => void;
  title?: string;
}

export function LoadingState({ title = 'Carregando', message = 'Preparando suas informações...' }: LoadingStateProps) {
  return (
    <View style={{ alignItems: 'center', gap: spacing.sm, padding: spacing.xl }}>
      <Mascot size={72} />
      <Text selectable style={[typography.subtitle, { color: colors.text, textAlign: 'center' }]}>
        {title}
      </Text>
      <Text selectable style={[typography.body, { color: colors.textMuted, textAlign: 'center' }]}>
        {message}
      </Text>
    </View>
  );
}

export function EmptyState({ title, message, actionLabel, onAction }: EmptyStateProps) {
  return (
    <View style={{ alignItems: 'center', gap: spacing.sm, padding: spacing.xl }}>
      <Icon name="search" color={colors.textSubtle} size={34} />
      <Text selectable style={[typography.subtitle, { color: colors.text, textAlign: 'center' }]}>
        {title}
      </Text>
      {message ? (
        <Text selectable style={[typography.body, { color: colors.textMuted, textAlign: 'center' }]}>
          {message}
        </Text>
      ) : null}
      {actionLabel && onAction ? <GhostButton onPress={onAction}>{actionLabel}</GhostButton> : null}
    </View>
  );
}

export function ErrorState({ title = 'Algo saiu do esperado', message, onRetry }: ErrorStateProps) {
  return (
    <View style={{ alignItems: 'center', gap: spacing.sm, padding: spacing.xl }}>
      <Icon name="warn" color={colors.modSolid} size={34} />
      <Text selectable style={[typography.subtitle, { color: colors.text, textAlign: 'center' }]}>
        {title}
      </Text>
      {message ? (
        <Text selectable style={[typography.body, { color: colors.textMuted, textAlign: 'center' }]}>
          {message}
        </Text>
      ) : null}
      {onRetry ? <GhostButton onPress={onRetry}>Tentar novamente</GhostButton> : null}
    </View>
  );
}
