import { EmptyStateScreen } from '../components';

interface PrivacyScreenProps {
  onBack: () => void;
}

export function PrivacyScreen({ onBack }: PrivacyScreenProps) {
  return (
    <EmptyStateScreen
      icon="shield"
      onBack={onBack}
      title="Privacidade e dados"
      subtitle="Configurações de privacidade em breve."
    />
  );
}
