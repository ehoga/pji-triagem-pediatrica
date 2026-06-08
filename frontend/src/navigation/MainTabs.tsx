import { View } from 'react-native';
import { AppTabBar, ErrorState, LoadingState } from '../components';
import { AboutScreen } from '../screens/AboutScreen';
import { ChildFormScreen } from '../screens/ChildFormScreen';
import { DevScreen } from '../screens/DevScreen';
import { HistoryScreen } from '../screens/HistoryScreen';
import { HomeScreen } from '../screens/HomeScreen';
import { NotificationsScreen } from '../screens/NotificationsScreen';
import { OrientationsScreen } from '../screens/OrientationsScreen';
import { PrivacyScreen } from '../screens/PrivacyScreen';
import { ProfileScreen } from '../screens/ProfileScreen';
import { QuizScreen } from '../screens/QuizScreen';
import { ResultScreen } from '../screens/ResultScreen';
import { SymptomsScreen } from '../screens/SymptomsScreen';
import { colors } from '../theme';
import { useAuth } from '../hooks/useAuth';
import { usePediatricData } from '../hooks/usePediatricData';
import { useEffect, useMemo, useState } from 'react';
import type { AppScreen, AppTab, ChildProfile, Symptom, TriageResult } from '../types/domain';

const tabToScreen: Record<AppTab, AppScreen> = {
  home: 'home',
  evaluate: 'symptoms',
  orientations: 'orientations',
  history: 'history',
  profile: 'profile',
};

function tabForScreen(screen: AppScreen): AppTab {
  if (['symptoms', 'quiz', 'result'].includes(screen)) return 'evaluate';
  if (screen === 'orientations') return 'orientations';
  if (screen === 'history') return 'history';
  if (['profile', 'about', 'child-add', 'notifications', 'privacy', 'dev'].includes(screen)) return 'profile';
  return 'home';
}

export function MainTabs() {
  const { user } = useAuth();
  const { data, error, isLoading, reload } = usePediatricData();
  const [childrenList, setChildrenList] = useState<ChildProfile[]>([]);
  const [selectedChild, setSelectedChild] = useState<ChildProfile | null>(null);
  const [selectedSymptom, setSelectedSymptom] = useState<Symptom | null>(null);
  const [result, setResult] = useState<TriageResult | null>(null);
  const [screen, setScreen] = useState<AppScreen>('home');
  const [returnScreen, setReturnScreen] = useState<AppScreen>('home');

  const activeTab = useMemo(() => tabForScreen(screen), [screen]);
  const showTabs = !['child-add', 'quiz', 'result', 'about', 'notifications', 'privacy', 'dev'].includes(screen);

  useEffect(() => {
    if (data.children.length) {
      setChildrenList((current) => (current.length ? current : data.children));
      setSelectedChild((current) => current || data.children[0] || null);
    }
    if (data.symptoms.length) {
      setSelectedSymptom((current) => current || data.symptoms[0] || null);
    }
  }, [data.children, data.symptoms]);

  function go(nextScreen: AppScreen) {
    if (nextScreen === 'child-add') {
      setReturnScreen(screen);
    }
    setScreen(nextScreen);
  }

  function changeTab(tab: AppTab) {
    setScreen(tabToScreen[tab]);
  }

  function handleSelectSymptom(symptom: Symptom) {
    setSelectedSymptom(symptom);
    setScreen('quiz');
  }

  function handleSaveChild(child: ChildProfile) {
    setChildrenList((current) => [...current, child]);
    setSelectedChild(child);
    setScreen('profile');
  }

  function renderScreen() {
    if (isLoading) {
      return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
          <LoadingState title="Carregando dados" message="Preparando sintomas, histórico e orientações." />
        </View>
      );
    }

    if (error) {
      return (
        <View style={{ flex: 1, justifyContent: 'center' }}>
          <ErrorState message={error} onRetry={reload} />
        </View>
      );
    }

    if (screen === 'child-add') {
      return <ChildFormScreen onBack={() => setScreen(returnScreen)} onSave={handleSaveChild} />;
    }

    if (screen === 'symptoms') {
      return (
        <SymptomsScreen
          onBack={() => setScreen('home')}
          onSelectSymptom={handleSelectSymptom}
          selectedChild={selectedChild}
          symptoms={data.symptoms}
        />
      );
    }

    if (screen === 'quiz') {
      return (
        <QuizScreen
          onBack={() => setScreen('symptoms')}
          onFinish={(nextResult) => {
            setResult(nextResult);
            setScreen('result');
          }}
          questions={data.questions}
          selectedChild={selectedChild}
          selectedSymptom={selectedSymptom}
        />
      );
    }

    if (screen === 'result') {
      return <ResultScreen onBackHome={() => setScreen('home')} onGoOrientations={() => setScreen('orientations')} result={result} risks={data.risks} />;
    }

    if (screen === 'orientations') {
      return <OrientationsScreen onBack={() => setScreen('home')} orientationCards={data.orientations} symptoms={data.symptoms} />;
    }

    if (screen === 'history') {
      return <HistoryScreen childrenList={childrenList} historyItems={data.history} onBack={() => setScreen('home')} />;
    }

    if (screen === 'profile') {
      return <ProfileScreen childrenList={childrenList} onGo={go} />;
    }

    if (screen === 'about') {
      return <AboutScreen onBack={() => setScreen('profile')} />;
    }

    if (screen === 'notifications') {
      return <NotificationsScreen onBack={() => setScreen('profile')} />;
    }

    if (screen === 'privacy') {
      return <PrivacyScreen onBack={() => setScreen('profile')} />;
    }

    if (screen === 'dev') {
      return <DevScreen onBack={() => setScreen('profile')} />;
    }

    return (
      <HomeScreen
        childrenList={childrenList}
        historyItems={data.history}
        onGo={go}
        onSelectChild={setSelectedChild}
        selectedChild={selectedChild}
        user={user}
      />
    );
  }

  return (
    <View style={{ backgroundColor: colors.background, flex: 1 }}>
      {renderScreen()}
      {showTabs ? <AppTabBar activeTab={activeTab} onChange={changeTab} /> : null}
    </View>
  );
}
