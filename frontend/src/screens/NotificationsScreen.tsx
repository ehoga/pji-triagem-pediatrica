import { EmptyStateScreen } from '../components';

interface NotificationsScreenProps {
  onBack: () => void;
}

export function NotificationsScreen({ onBack }: NotificationsScreenProps) {
  return (
    <EmptyStateScreen
      icon="bell"
      onBack={onBack}
      title="Notificações"
      subtitle="Controle de notificações será habilitado em breve."
    />
  );
}
