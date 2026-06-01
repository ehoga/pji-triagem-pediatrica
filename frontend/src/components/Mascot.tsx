import Svg, { Circle, Defs, Path, RadialGradient, Stop } from 'react-native-svg';
import { colors } from '../theme';
import type { MascotMood } from '../types/domain';

interface MascotProps {
  mood?: MascotMood;
  size?: number;
}

export function Mascot({ size = 72, mood = 'calm' }: MascotProps) {
  const accent = mood === 'alert' ? colors.highSolid : mood === 'watch' ? colors.modSolid : colors.primary;
  const endColor = mood === 'alert' ? colors.highSoft : mood === 'watch' ? colors.modSoft : colors.mascotCalmEnd;
  const mouth = mood === 'alert' ? 'M30 50q10 -5 20 0' : 'M30 48q10 6 20 0';
  const gradientId = `mascot-${mood}`;

  return (
    <Svg width={size} height={size} viewBox="0 0 80 80" accessibilityRole="image">
      <Defs>
        <RadialGradient id={gradientId} cx="50%" cy="40%" rx="60%" ry="60%">
          <Stop offset="0%" stopColor={colors.surface} />
          <Stop offset="100%" stopColor={endColor} />
        </RadialGradient>
      </Defs>
      <Circle
        cx="40"
        cy="40"
        r="34"
        fill={`url(#${gradientId})`}
        stroke={accent}
        strokeOpacity="0.25"
        strokeWidth="1.5"
      />
      <Circle cx="30" cy="36" r="2.6" fill={colors.navy} />
      <Circle cx="50" cy="36" r="2.6" fill={colors.navy} />
      <Path d={mouth} stroke={colors.navy} strokeWidth="2.4" fill="none" strokeLinecap="round" />
      <Circle cx="22" cy="44" r="2.5" fill={accent} opacity="0.5" />
      <Circle cx="58" cy="44" r="2.5" fill={accent} opacity="0.5" />
    </Svg>
  );
}
