import {
  feverQuiz,
  historyItems,
  initialChildren,
  orientationCards,
  riskContent,
  symptoms,
} from '../mocks/appMock';
import type {
  ChildProfile,
  HistoryItem,
  OrientationCardItem,
  PediatricDemoData,
  RiskContentMap,
  Symptom,
  TriageQuestion,
} from '../types/domain';

function wait(ms = 180): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function mockResponse<T>(payload: T): Promise<T> {
  await wait();
  return payload;
}

export async function getChildren(): Promise<ChildProfile[]> {
  return mockResponse(initialChildren);
}

export async function getSymptoms(): Promise<Symptom[]> {
  return mockResponse(symptoms);
}

export async function getQuizQuestions(): Promise<TriageQuestion[]> {
  return mockResponse(feverQuiz);
}

export async function getRiskContent(): Promise<RiskContentMap> {
  return mockResponse(riskContent);
}

export async function getHistory(): Promise<HistoryItem[]> {
  return mockResponse(historyItems);
}

export async function getOrientationCards(): Promise<OrientationCardItem[]> {
  return mockResponse(orientationCards);
}

export async function getPediatricDemoData(): Promise<PediatricDemoData> {
  const [children, symptomList, questions, risks, history, orientations] = await Promise.all([
    getChildren(),
    getSymptoms(),
    getQuizQuestions(),
    getRiskContent(),
    getHistory(),
    getOrientationCards(),
  ]);

  return {
    children,
    history,
    orientations,
    questions,
    risks,
    symptoms: symptomList,
  };
}
