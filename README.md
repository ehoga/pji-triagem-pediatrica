# PJI - Triagem Pediátrica

Projeto Integrador para sistema de **triagem pediátrica**, contendo frontend mobile e backend em monorepo.

## Stack

- **Frontend:** React Native com [Expo](https://expo.dev/)
- **Backend:** Java 21 + Spring Boot 3 (Maven)
- **Banco:** H2 (desenvolvimento) — Postgres recomendado para produção

## Estrutura do projeto

```
pji-triagem-pediatrica/
├── frontend/          # App React Native (Expo)
├── backend/           # API Spring Boot
├── .gitignore
├── README.md
└── CONTRIBUTING.md    # Regras de contribuição (PRs, commits, branches)
```

## Pré-requisitos

| Ferramenta | Versão | Uso |
|---|---|---|
| Node.js | 20+ | Frontend e CI |
| npm ou yarn | — | Frontend |
| Java JDK | 21 | Backend |
| Expo Go (app) | — | Testar no celular sem build nativa |

## Como rodar

### Backend
```bash
cd backend
./mvnw spring-boot:run
```
A API sobe em `http://localhost:8080`. Console do H2 disponível em `/h2-console`.

### Frontend
```bash
cd frontend
npm install     # apenas na primeira vez
npm start
```
Após iniciar, escaneie o QR code com o app **Expo Go** ou pressione `w` para abrir no navegador.

Variáveis úteis:

```bash
EXPO_PUBLIC_API_URL=http://<IP-DA-SUA-MAQUINA>:8080
EXPO_PUBLIC_USE_MOCK_AUTH=true
```

Use o IP local da máquina quando o app estiver em um celular físico na mesma rede Wi-Fi.

## Qualidade do frontend

```bash
cd frontend
npm run doctor
```

O workflow `.github/workflows/frontend.yml` executa `npm ci` e `npm run doctor` em PRs que alteram o frontend.

## Contrato da API

O contrato inicial do MVP está em [`docs/openapi.yaml`](./docs/openapi.yaml). O frontend deve consumir endpoints refletidos nesse arquivo; quando a API real ainda não existir, use mocks atrás de uma camada de service.

## Build iOS e TestFlight

O projeto já contém `frontend/eas.json` com profile `preview` para build iOS distribuível via TestFlight.

Pré-requisitos externos:

- Conta Expo autenticada com `npx eas-cli login`.
- Projeto vinculado com `npx eas-cli init`.
- Apple Developer Program ativo.
- Bundle ID acordado: `com.peditriagem.app`.
- App criado no App Store Connect.

Comandos:

```bash
cd frontend
EXPO_PUBLIC_API_URL=http://<IP-DA-SUA-MAQUINA>:8080 npm run build:ios:preview
npm run submit:ios:latest
```

Após o envio, configure os Internal Testers no App Store Connect e compartilhe o convite do TestFlight com o time e a banca. O backend continua rodando localmente na máquina da apresentação.

## Demonstração

O roteiro de apresentação está em [`docs/DEMO.md`](./docs/DEMO.md), com cenários de baixo risco, risco moderado e alto risco com red flag.

## Fluxo de trabalho (importante)

- **Branches protegidas:** `main` e `dev` — nenhum push direto, **apenas via Pull Request**.
- **`main`** representa código pronto para release.
- **`dev`** é a branch de integração — todo trabalho novo é mergeado aqui primeiro.
- Cada feature/fix sai de `dev` em uma branch própria (`feature/...`, `fix/...`, `chore/...`).
- PRs exigem **1 aprovação** antes do merge.

Veja [CONTRIBUTING.md](./CONTRIBUTING.md) para detalhes do fluxo, padrões de commit e de PR.
