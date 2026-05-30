import { useState } from 'react';
import { Pressable, ScrollView, Text, View } from 'react-native';
import { Card, Icon, PrimaryButton, ScreenHeader, TextField } from '../components';
import { colors, radii, riskPalette, shadows, spacing, typography } from '../theme';
import type { AgeUnit, ChildProfile, RiskTone } from '../types/domain';

type AvatarTint = Extract<RiskTone, 'primary' | 'low' | 'mod' | 'high'>;

const avatarOptions: { id: AvatarTint; tint: string }[] = [
  { id: 'primary', tint: colors.primarySoft },
  { id: 'low', tint: riskPalette.low.softer },
  { id: 'mod', tint: riskPalette.mod.softer },
  { id: 'high', tint: riskPalette.high.softer },
];

interface ChildFormScreenProps {
  onBack: () => void;
  onSave: (child: ChildProfile) => void;
}

export function ChildFormScreen({ onBack, onSave }: ChildFormScreenProps) {
  const [name, setName] = useState('');
  const [ageValue, setAgeValue] = useState('');
  const [ageUnit, setAgeUnit] = useState<AgeUnit>('anos');
  const [weight, setWeight] = useState('');
  const [tint, setTint] = useState<AvatarTint>('primary');
  const [error, setError] = useState<string | null>(null);

  function handleSave() {
    setError(null);
    const trimmedName = name.trim();
    if (!trimmedName || !ageValue.trim()) {
      setError('Informe nome e idade da criança.');
      return;
    }

    const initials = trimmedName
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part.charAt(0))
      .join('')
      .toUpperCase();

    const child: ChildProfile = {
      id: `${trimmedName.toLowerCase().replace(/\s+/g, '-')}-${Date.now()}`,
      name: trimmedName,
      ageValue,
      ageUnit,
      age: `${ageValue} ${ageUnit}`,
      weight: weight ? `${weight} kg` : 'Peso não informado',
      initials,
      tint,
    };

    onSave(child);
  }

  return (
    <ScrollView contentContainerStyle={{ gap: spacing.md, paddingBottom: spacing.xxl }} contentInsetAdjustmentBehavior="automatic">
      <ScreenHeader title="Cadastrar criança" onBack={onBack} />
      <View style={{ alignItems: 'center', gap: spacing.sm }}>
        <View
          style={[
            {
              alignItems: 'center',
              backgroundColor: avatarOptions.find((item) => item.id === tint)?.tint || colors.primarySoft,
              borderRadius: radii.avatar,
              height: 80,
              justifyContent: 'center',
              width: 80,
            },
            shadows.card,
          ]}
        >
          <Text selectable={false} style={[typography.title, { color: colors.navy }]}>
            {(name.trim().slice(0, 2) || 'PT').toUpperCase()}
          </Text>
        </View>
        <View style={{ flexDirection: 'row', gap: spacing.xs }}>
          {avatarOptions.map((option) => (
            <Pressable
              accessibilityRole="button"
              key={option.id}
              onPress={() => setTint(option.id)}
              style={[
                {
                  alignItems: 'center',
                  backgroundColor: tint === option.id ? colors.navy : colors.surface,
                  borderRadius: radii.md,
                  height: 34,
                  justifyContent: 'center',
                  width: 34,
                },
                shadows.card,
              ]}
            >
              <View style={{ backgroundColor: option.tint, borderRadius: radii.pill, height: 14, width: 14 }} />
            </Pressable>
          ))}
        </View>
      </View>

      <Card style={{ marginHorizontal: spacing.lg }} contentStyle={{ gap: spacing.md }}>
        <TextField label="Nome da criança" onChangeText={setName} placeholder="Ex.: Maria" value={name} />
        <View style={{ gap: spacing.xs }}>
          <TextField
            inputMode="numeric"
            label="Idade"
            onChangeText={setAgeValue}
            placeholder="Ex.: 4"
            value={ageValue}
          />
          <View
            style={[
              {
                alignSelf: 'flex-start',
                backgroundColor: colors.surface,
                borderRadius: radii.lg,
                flexDirection: 'row',
                gap: spacing.xxs,
                padding: spacing.xxs,
              },
              shadows.card,
            ]}
          >
            {(['meses', 'anos'] satisfies AgeUnit[]).map((unit) => {
              const selected = ageUnit === unit;
              return (
                <Pressable
                  accessibilityRole="button"
                  key={unit}
                  onPress={() => setAgeUnit(unit)}
                  style={{
                    backgroundColor: selected ? colors.primary : 'transparent',
                    borderRadius: radii.sm,
                    paddingHorizontal: spacing.sm,
                    paddingVertical: spacing.xs,
                  }}
                >
                  <Text selectable={false} style={[typography.caption, { color: selected ? colors.inverseText : colors.text }]}>
                    {unit}
                  </Text>
                </Pressable>
              );
            })}
          </View>
        </View>
        <TextField
          hint="Ajuda em cálculos futuros de dose e hidratação."
          inputMode="decimal"
          label="Peso (opcional)"
          onChangeText={setWeight}
          placeholder="Ex.: 17"
          value={weight}
        />
        {error ? (
          <Text selectable style={[typography.caption, { color: colors.dangerText }]}>
            {error}
          </Text>
        ) : null}
        <PrimaryButton onPress={handleSave} icon={<Icon name="check" color={colors.inverseText} size={18} />}>
          Salvar criança
        </PrimaryButton>
      </Card>
    </ScrollView>
  );
}
