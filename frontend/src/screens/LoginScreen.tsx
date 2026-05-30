import { useState } from 'react';
import { KeyboardAvoidingView, Platform, ScrollView, Text, View } from 'react-native';
import { Card, GhostButton, Icon, Mascot, PrimaryButton, TextField } from '../components';
import { getDemoCredentials } from '../services/authService';
import { colors, spacing, typography } from '../theme';
import { useAuth } from '../hooks/useAuth';

interface LoginScreenProps {
  onNavigate: (screen: 'register') => void;
}

function getSubmitErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : 'Não foi possível entrar agora.';
}

export function LoginScreen({ onNavigate }: LoginScreenProps) {
  const { authError, isSubmitting, login } = useAuth();
  const demoCredentials = getDemoCredentials();
  const [email, setEmail] = useState(demoCredentials.email);
  const [password, setPassword] = useState(demoCredentials.password);
  const [localError, setLocalError] = useState<string | null>(null);

  async function handleSubmit() {
    setLocalError(null);
    if (!email.trim() || !password) {
      setLocalError('Informe email e senha para continuar.');
      return;
    }

    try {
      await login({ email, password });
    } catch (error) {
      setLocalError(getSubmitErrorMessage(error));
    }
  }

  return (
    <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : undefined} style={{ flex: 1 }}>
      <ScrollView
        contentContainerStyle={{
          flexGrow: 1,
          gap: spacing.lg,
          justifyContent: 'center',
          padding: spacing.lg,
          paddingTop: spacing.xxl,
        }}
        keyboardShouldPersistTaps="handled"
      >
        <View style={{ alignItems: 'center', gap: spacing.sm }}>
          <Mascot size={88} />
          <Text selectable style={[typography.title, { color: colors.text, textAlign: 'center' }]}>
            PediTriagem
          </Text>
          <Text selectable style={[typography.body, { color: colors.textMuted, textAlign: 'center' }]}>
            Apoio inicial para pais e cuidadores acompanharem sintomas pediátricos com calma.
          </Text>
        </View>

        <Card contentStyle={{ gap: spacing.md }}>
          <TextField
            autoCapitalize="none"
            autoComplete="email"
            inputMode="email"
            label="Email"
            onChangeText={setEmail}
            placeholder="voce@email.com"
            value={email}
          />
          <TextField
            label="Senha"
            onChangeText={setPassword}
            placeholder="Sua senha"
            secureTextEntry
            value={password}
          />
          {localError || authError ? (
            <Text selectable style={[typography.caption, { color: colors.dangerText }]}>
              {localError || authError}
            </Text>
          ) : null}
          <PrimaryButton loading={isSubmitting} onPress={handleSubmit} icon={<Icon name="lock" color={colors.inverseText} size={18} />}>
            Entrar
          </PrimaryButton>
          <GhostButton onPress={() => onNavigate('register')} icon={<Icon name="userPlus" size={18} />}>
            Criar conta
          </GhostButton>
        </Card>

        <Card padding={spacing.sm} contentStyle={{ flexDirection: 'row', gap: spacing.sm }}>
          <Icon name="shield" color={colors.primary} size={20} />
          <Text selectable style={[typography.caption, { color: colors.textMuted, flex: 1 }]}>
            Demo local habilitada. Use qualquer email com senha de 4+ caracteres ou os dados preenchidos.
          </Text>
        </Card>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
