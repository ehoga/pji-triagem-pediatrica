import * as Icons from 'lucide-react-native';
import type { ComponentProps } from 'react';
import { colors } from '../theme';
import type { IconName } from '../types/domain';

const iconMap = {
  activity: Icons.Activity,
  alert: Icons.CircleAlert,
  back: Icons.ArrowLeft,
  baby: Icons.Baby,
  bell: Icons.Bell,
  book: Icons.BookOpen,
  check: Icons.Check,
  chevronRight: Icons.ChevronRight,
  clipboard: Icons.ClipboardList,
  close: Icons.X,
  cough: Icons.Wind,
  drop: Icons.Droplets,
  heart: Icons.HeartPulse,
  history: Icons.History,
  home: Icons.Home,
  info: Icons.Info,
  lock: Icons.Lock,
  logout: Icons.LogOut,
  phone: Icons.Phone,
  pill: Icons.Pill,
  plus: Icons.Plus,
  profile: Icons.User,
  rash: Icons.Sparkles,
  search: Icons.Search,
  settings: Icons.Settings,
  shield: Icons.Shield,
  stetho: Icons.Stethoscope,
  thermo: Icons.Thermometer,
  trauma: Icons.Bandage,
  userPlus: Icons.UserPlus,
  vomit: Icons.CircleOff,
  warn: Icons.TriangleAlert,
} satisfies Record<IconName, typeof Icons.Circle>;

type IconProps = Omit<ComponentProps<typeof Icons.Circle>, 'color' | 'size' | 'strokeWidth'> & {
  color?: string;
  name: IconName | string;
  size?: number;
  strokeWidth?: number;
};

export function Icon({ name, size = 22, color = colors.navy, strokeWidth = 2, ...props }: IconProps) {
  const IconComponent = Object.hasOwn(iconMap, name) ? iconMap[name as IconName] : Icons.Circle;
  return <IconComponent size={size} color={color} strokeWidth={strokeWidth} {...props} />;
}
