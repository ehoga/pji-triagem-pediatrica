import { ScrollView, Text, View } from 'react-native';
import { Card, GhostButton, Mascot, Pill, PrimaryButton, ProgressBar, ScreenHeader } from '../components';
import { colors, riskPalette, spacing, typography } from '../theme';

interface DevScreenProps {
  onBack: () => void;
}

export function DevScreen({ onBack }: DevScreenProps) {
  return (
    <ScrollView contentContainerStyle={{ gap: spacing.md, paddingBottom: spacing.xxl }} contentInsetAdjustmentBehavior="automatic">
      <ScreenHeader title="Storybook visual" onBack={onBack} />
      <View style={{ gap: spacing.md, paddingHorizontal: spacing.lg }}>
        <Card contentStyle={{ gap: spacing.sm }}>
          <Text selectable style={[typography.subtitle, { color: colors.text }]}>
            Tokens de risco
          </Text>
          <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: spacing.xs }}>
            <Pill tone="primary">Primário</Pill>
            <Pill tone="low" withDot>{riskPalette.low.label}</Pill>
            <Pill tone="mod" withDot>{riskPalette.mod.label}</Pill>
            <Pill tone="high" withDot>{riskPalette.high.label}</Pill>
            <Pill tone="neutral">Neutro</Pill>
          </View>
        </Card>

        <Card contentStyle={{ gap: spacing.sm }}>
          <Text selectable style={[typography.subtitle, { color: colors.text }]}>
            Botões
          </Text>
          <PrimaryButton>Primary</PrimaryButton>
          <PrimaryButton tone="navy">Navy</PrimaryButton>
          <PrimaryButton tone="low">Baixo risco</PrimaryButton>
          <PrimaryButton tone="mod">Moderado</PrimaryButton>
          <PrimaryButton tone="high">Alto risco</PrimaryButton>
          <GhostButton>Ghost button</GhostButton>
        </Card>

        <Card contentStyle={{ gap: spacing.sm }}>
          <Text selectable style={[typography.subtitle, { color: colors.text }]}>
            Mascote
          </Text>
          <View style={{ flexDirection: 'row', justifyContent: 'space-around' }}>
            <Mascot mood="calm" />
            <Mascot mood="watch" />
            <Mascot mood="alert" />
          </View>
          <ProgressBar value={3} total={6} />
        </Card>
      </View>
    </ScrollView>
  );
}
