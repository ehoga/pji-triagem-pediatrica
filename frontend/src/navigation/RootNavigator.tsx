import { View } from 'react-native';
import { colors } from '../theme';
import { useAuth } from '../hooks/useAuth';
import { AuthStack } from './AuthStack';
import { MainTabs } from './MainTabs';
import { LoadingScreen } from '../screens/LoadingScreen';

export function RootNavigator() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <LoadingScreen />;
  }

  return (
    <View style={{ backgroundColor: colors.background, flex: 1 }}>
      {isAuthenticated ? <MainTabs /> : <AuthStack />}
    </View>
  );
}
