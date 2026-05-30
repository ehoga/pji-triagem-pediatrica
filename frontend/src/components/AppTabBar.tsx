import { Pressable, Text, View } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { colors, radii, shadows, spacing, typography } from '../theme';
import { Icon } from './Icon';
import type { IconName } from '../types/domain';

export type AppTabId = 'home' | 'evaluate' | 'orientations' | 'history' | 'profile';

interface TabItem {
  icon: IconName;
  id: AppTabId;
  isMain?: boolean;
  label: string;
}

const tabs: TabItem[] = [
  { id: 'home', label: 'Início', icon: 'home' },
  { id: 'evaluate', label: 'Avaliar', icon: 'stetho', isMain: true },
  { id: 'orientations', label: 'Orientações', icon: 'book' },
  { id: 'history', label: 'Histórico', icon: 'history' },
  { id: 'profile', label: 'Perfil', icon: 'profile' },
];

interface AppTabBarProps {
  activeTab: AppTabId;
  onChange: (tab: AppTabId) => void;
}

export function AppTabBar({ activeTab, onChange }: AppTabBarProps) {
  return (
    <View
      style={[
        {
          alignItems: 'center',
          backgroundColor: colors.tabBar,
          borderColor: colors.hairline,
          borderRadius: radii.hero,
          borderWidth: 1,
          bottom: spacing.md,
          flexDirection: 'row',
          gap: spacing.xxs,
          left: spacing.sm,
          paddingHorizontal: spacing.xs,
          paddingVertical: spacing.xs,
          position: 'absolute',
          right: spacing.sm,
        },
        shadows.lg,
      ]}
    >
      {tabs.map((tab) => {
        const selected = activeTab === tab.id;

        if (tab.isMain) {
          return (
            <Pressable
              accessibilityRole="tab"
              accessibilityState={{ selected }}
              key={tab.id}
              onPress={() => onChange(tab.id)}
              style={({ pressed }) => ({
                alignItems: 'center',
                flex: 1,
                gap: spacing.xxs,
                opacity: pressed ? 0.78 : 1,
                transform: [{ translateY: -20 }],
              })}
            >
              <LinearGradient
                colors={[colors.secondary, colors.primary]}
                style={{
                  alignItems: 'center',
                  borderRadius: radii.card,
                  height: 56,
                  justifyContent: 'center',
                  width: 56,
                }}
              >
                <Icon name={tab.icon} color={colors.inverseText} size={26} strokeWidth={2.2} />
              </LinearGradient>
              <Text selectable={false} numberOfLines={1} style={[typography.caption, { color: colors.primaryDark }]}>
                {tab.label}
              </Text>
            </Pressable>
          );
        }

        return (
          <Pressable
            accessibilityRole="tab"
            accessibilityState={{ selected }}
            key={tab.id}
            onPress={() => onChange(tab.id)}
            style={({ pressed }) => ({
              alignItems: 'center',
              flex: 1,
              gap: spacing.xxs,
              opacity: pressed ? 0.72 : 1,
              paddingVertical: spacing.xs,
            })}
          >
            <Icon name={tab.icon} color={selected ? colors.primary : colors.textSubtle} size={22} />
            <Text
              selectable={false}
              numberOfLines={1}
              adjustsFontSizeToFit
              style={[typography.caption, { color: selected ? colors.primary : colors.textSubtle, fontSize: 10 }]}
            >
              {tab.label}
            </Text>
          </Pressable>
        );
      })}
    </View>
  );
}
