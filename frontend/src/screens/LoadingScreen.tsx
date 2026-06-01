import { View } from 'react-native';
import { colors } from '../theme';
import { LoadingState } from '../components';

export function LoadingScreen() {
  return (
    <View style={{ alignItems: 'center', backgroundColor: colors.background, flex: 1, justifyContent: 'center' }}>
      <LoadingState title="Abrindo PediTriagem" message="Validando sua sessão com segurança." />
    </View>
  );
}
