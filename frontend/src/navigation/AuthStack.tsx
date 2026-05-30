import { useState } from 'react';
import { LoginScreen } from '../screens/LoginScreen';
import { RegisterScreen } from '../screens/RegisterScreen';

type AuthScreen = 'login' | 'register';

export function AuthStack() {
  const [screen, setScreen] = useState<AuthScreen>('login');

  if (screen === 'register') {
    return <RegisterScreen onBack={() => setScreen('login')} />;
  }

  return <LoginScreen onNavigate={setScreen} />;
}
