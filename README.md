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
| Node.js | 18+ | Frontend |
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

## Fluxo de trabalho (importante)

- **Branches protegidas:** `main` e `dev` — nenhum push direto, **apenas via Pull Request**.
- **`main`** representa código pronto para release.
- **`dev`** é a branch de integração — todo trabalho novo é mergeado aqui primeiro.
- Cada feature/fix sai de `dev` em uma branch própria (`feature/...`, `fix/...`, `chore/...`).
- PRs exigem **1 aprovação** antes do merge.

Veja [CONTRIBUTING.md](./CONTRIBUTING.md) para detalhes do fluxo, padrões de commit e de PR.
