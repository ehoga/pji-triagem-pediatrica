import { useState } from 'react';
import { Pressable, ScrollView, Text, View } from 'react-native';
import { Card, EmptyState, Icon, Mascot, ProgressBar, ScreenHeader } from '../components';
import { colors, radii, shadows, spacing, typography } from '../theme';
import type { ChildProfile, Symptom, TriageAnswers, TriageQuestion, TriageResult, YesNoAnswer } from '../types/domain';
import { calculateRisk } from '../utils/risk';

interface QuizScreenProps {
  onBack: () => void;
  onFinish: (result: TriageResult) => void;
  questions?: TriageQuestion[];
  selectedChild: ChildProfile | null;
  selectedSymptom: Symptom | null;
}

const yesNoOptions: { id: YesNoAnswer; label: string; wide?: boolean }[] = [
  { id: 'yes', label: 'Sim' },
  { id: 'no', label: 'Não' },
  { id: 'dunno', label: 'Não tenho certeza', wide: true },
];

export function QuizScreen({ onBack, onFinish, questions = [], selectedChild, selectedSymptom }: QuizScreenProps) {
  const [step, setStep] = useState(0);
  const [answers, setAnswers] = useState<TriageAnswers>({});
  const total = questions.length;
  const current = questions[step];
  const selectedAnswer = answers[step];

  if (!current) {
    return (
      <ScrollView contentContainerStyle={{ flexGrow: 1 }} contentInsetAdjustmentBehavior="automatic">
        <ScreenHeader title="Triagem" onBack={onBack} />
        <EmptyState title="Questionário indisponível" message="Não há perguntas configuradas para este sintoma." />
      </ScrollView>
    );
  }

  function selectAnswer(value: string) {
    const nextAnswers = { ...answers, [step]: value };
    setAnswers(nextAnswers);

    setTimeout(() => {
      if (step + 1 < total) {
        setStep((currentStep) => currentStep + 1);
        return;
      }

      const result = calculateRisk(questions, nextAnswers);
      onFinish({
        ...result,
        child: selectedChild,
        symptom: selectedSymptom,
        answeredAt: new Date().toISOString(),
      });
    }, 140);
  }

  function goBack() {
    if (step > 0) {
      setStep((currentStep) => currentStep - 1);
      return;
    }
    onBack();
  }

  return (
    <ScrollView contentContainerStyle={{ flexGrow: 1, paddingBottom: spacing.xl }} contentInsetAdjustmentBehavior="automatic">
      <ScreenHeader
        title={selectedSymptom?.name || 'Triagem'}
        subtitle={selectedChild ? `${selectedChild.name} · ${selectedChild.age}` : undefined}
        onBack={goBack}
      />
      <ProgressBar value={step + 1} total={total} />

      <View style={{ flex: 1, gap: spacing.lg, paddingHorizontal: spacing.lg, paddingTop: spacing.xs }}>
        <View style={{ alignItems: 'flex-start', flexDirection: 'row', gap: spacing.sm }}>
          <Mascot size={46} />
          <Card
            padding={spacing.sm}
            style={{ borderTopLeftRadius: radii.xxs, flex: 1 }}
            contentStyle={{ gap: spacing.xs }}
          >
            <Text selectable style={[typography.subtitle, { color: colors.text }]}>
              {current.q}
            </Text>
            {current.sub ? (
              <Text selectable style={[typography.body, { color: colors.textMuted }]}>
                {current.sub}
              </Text>
            ) : null}
          </Card>
        </View>

        {current.type === 'options' ? (
          <View style={{ gap: spacing.xs }}>
            {current.options.map((option) => {
              const selected = selectedAnswer === option.id;
              return (
                <Pressable
                  accessibilityRole="button"
                  key={option.id}
                  onPress={() => selectAnswer(option.id)}
                  style={[
                    {
                      alignItems: 'center',
                      backgroundColor: selected ? colors.primary : colors.surface,
                      borderRadius: radii.xl,
                      flexDirection: 'row',
                      justifyContent: 'space-between',
                      minHeight: 56,
                      paddingHorizontal: spacing.md,
                    },
                    !selected && shadows.card,
                  ]}
                >
                  <Text selectable={false} style={[typography.bodyStrong, { color: selected ? colors.inverseText : colors.text, flex: 1 }]}>
                    {option.label}
                  </Text>
                  {selected ? <Icon name="check" color={colors.inverseText} size={18} /> : null}
                </Pressable>
              );
            })}
          </View>
        ) : (
          <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: spacing.xs }}>
            {yesNoOptions.map((option) => {
              const selected = selectedAnswer === option.id;
              return (
                <Pressable
                  accessibilityRole="button"
                  key={option.id}
                  onPress={() => selectAnswer(option.id)}
                  style={[
                    {
                      alignItems: 'center',
                      backgroundColor: selected ? colors.primary : colors.surface,
                      borderColor: option.wide ? colors.hairline : colors.surface,
                      borderRadius: radii.xl,
                      borderWidth: option.wide ? 1.5 : 0,
                      flexBasis: option.wide ? '100%' : '48%',
                      flexGrow: option.wide ? 1 : 0,
                      justifyContent: 'center',
                      minHeight: option.wide ? 50 : 76,
                      paddingHorizontal: spacing.md,
                    },
                    !selected && shadows.card,
                  ]}
                >
                  <Text selectable={false} style={[typography.subtitle, { color: selected ? colors.inverseText : colors.text, textAlign: 'center' }]}>
                    {option.label}
                  </Text>
                </Pressable>
              );
            })}
          </View>
        )}

        <Text selectable style={[typography.caption, { color: colors.textSubtle, textAlign: 'center' }]}>
          Suas respostas ficam neste dispositivo durante a demonstração.
        </Text>
      </View>
    </ScrollView>
  );
}
