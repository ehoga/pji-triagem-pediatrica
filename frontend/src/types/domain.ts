export type RiskLevel = 'low' | 'mod' | 'high';
export type RiskTone = RiskLevel | 'primary' | 'neutral';
export type AgeUnit = 'meses' | 'anos';
export type MascotMood = 'calm' | 'watch' | 'alert';
export type YesNoAnswer = 'yes' | 'no' | 'dunno';
export type IconName =
  | 'activity'
  | 'alert'
  | 'back'
  | 'baby'
  | 'bell'
  | 'book'
  | 'check'
  | 'chevronRight'
  | 'clipboard'
  | 'close'
  | 'cough'
  | 'drop'
  | 'heart'
  | 'history'
  | 'home'
  | 'info'
  | 'lock'
  | 'logout'
  | 'phone'
  | 'pill'
  | 'plus'
  | 'profile'
  | 'rash'
  | 'search'
  | 'settings'
  | 'shield'
  | 'stetho'
  | 'thermo'
  | 'trauma'
  | 'userPlus'
  | 'vomit'
  | 'warn';
export type AppTab = 'home' | 'evaluate' | 'orientations' | 'history' | 'profile';
export type AppScreen =
  | 'home'
  | 'symptoms'
  | 'quiz'
  | 'result'
  | 'orientations'
  | 'history'
  | 'profile'
  | 'about'
  | 'child-add'
  | 'dev';

export interface ChildProfile {
  id: string;
  name: string;
  ageValue?: string;
  ageUnit?: AgeUnit;
  age: string;
  weight: string;
  initials: string;
  tint: RiskTone;
}

export interface Symptom {
  id: string;
  name: string;
  desc: string;
  icon: IconName;
  tone: RiskTone;
}

export interface QuizOption {
  id: string;
  label: string;
  risk: number;
}

export interface OptionsQuestion {
  q: string;
  sub?: string;
  type: 'options';
  options: QuizOption[];
  redFlag?: boolean;
}

export interface YesNoQuestion {
  q: string;
  sub?: string;
  type: 'yesno';
  weights: Record<YesNoAnswer, number>;
  redFlag?: boolean;
}

export type TriageQuestion = OptionsQuestion | YesNoQuestion;
export type TriageAnswers = Record<number, string>;

export interface TriageResult {
  risk: RiskLevel;
  score: number;
  hasRedFlag: boolean;
  child?: ChildProfile | null;
  symptom?: Symptom | null;
  answeredAt?: string;
}

export interface RiskContent {
  title: string;
  message: string;
  cta: string;
  icon: IconName;
  mood: MascotMood;
  actions: string[];
}

export type RiskContentMap = Record<RiskLevel, RiskContent>;

export interface HistoryItem {
  id: string;
  child: string;
  symptom: string;
  date: string;
  risk: RiskLevel;
}

export interface OrientationCardItem {
  id: string;
  title: string;
  subtitle: string;
  icon: IconName;
  tone: RiskTone;
}

export interface PreferenceItem {
  icon: string;
  label: string;
  go?: AppScreen;
}

export interface PediatricDemoData {
  children: ChildProfile[];
  history: HistoryItem[];
  orientations: OrientationCardItem[];
  questions: TriageQuestion[];
  risks: RiskContentMap;
  symptoms: Symptom[];
}
