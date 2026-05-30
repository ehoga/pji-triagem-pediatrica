import { ScrollView, Text, View } from 'react-native';
import { EmptyState, Mascot, ScreenHeader, SymptomCard } from '../components';
import { colors, spacing, typography } from '../theme';
import type { ChildProfile, Symptom } from '../types/domain';

interface SymptomsScreenProps {
  onBack: () => void;
  onSelectSymptom: (symptom: Symptom) => void;
  selectedChild: ChildProfile | null;
  symptoms?: Symptom[];
}

export function SymptomsScreen({ onBack, onSelectSymptom, selectedChild, symptoms = [] }: SymptomsScreenProps) {
  return (
    <ScrollView contentContainerStyle={{ gap: spacing.md, paddingBottom: spacing.tabContentBottom }} contentInsetAdjustmentBehavior="automatic">
      <ScreenHeader
        title="Selecionar sintoma"
        subtitle={selectedChild ? `${selectedChild.name} · ${selectedChild.age}` : undefined}
        onBack={onBack}
      />
      <View style={{ alignItems: 'center', flexDirection: 'row', gap: spacing.md, paddingHorizontal: spacing.lg }}>
        <Mascot size={54} />
        <View style={{ flex: 1 }}>
          <Text selectable style={[typography.subtitle, { color: colors.text }]}>
            Qual é o principal sintoma agora?
          </Text>
          <Text selectable style={[typography.body, { color: colors.textMuted, marginTop: spacing.xxs }]}>
            Escolha o sinal que mais preocupa para iniciar a triagem guiada.
          </Text>
        </View>
      </View>

      <View style={{ gap: spacing.xs, paddingHorizontal: spacing.lg }}>
        {symptoms.length ? (
          symptoms.map((symptom) => (
            <SymptomCard key={symptom.id} symptom={symptom} onPress={() => onSelectSymptom(symptom)} />
          ))
        ) : (
          <EmptyState title="Nenhum sintoma disponível" message="Tente novamente quando o catálogo for carregado." />
        )}
      </View>
    </ScrollView>
  );
}
