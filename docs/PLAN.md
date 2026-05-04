# Plano de Features — Sistema de Triagem Pediátrica

> Plano de alto nível com features, ordem de execução, prioridades e dependências.
> Para detalhamento de **tasks por feature**, ver [`FEATURES.md`](./FEATURES.md).

---

## 1. Visão geral do produto

App mobile chamado **PediTriagem** que orienta **pais e responsáveis** na avaliação inicial de sintomas de crianças (0-12 anos), classificando o nível de urgência e sugerindo próximos passos.

**Atores:** pai/responsável (ator único do MVP).

**Fluxo principal:**
```
Login → Home → Selecionar/cadastrar criança → Selecionar sintoma → Responder questionário → Receber classificação + orientação → (Histórico)
```

**Classificação de risco:**
| Cor | Nível | Significado |
|---|---|---|
| 🔴 | Alto risco | Procurar pronto-socorro imediatamente |
| 🟡 | Risco moderado | Procurar pediatra ou PA em até 24h |
| 🟢 | Baixo risco | Observação domiciliar com orientações |

---

## 2. Decisões de escopo do MVP

| Decisão | Valor |
|---|---|
| **Sintomas com fluxograma** | Todos os **9** (febre, tosse, vômitos, diarreia, dor abdominal, falta de ar, manchas na pele, trauma leve, dor de ouvido) |
| **Autenticação** | Completa: cadastro + login + JWT |
| **Histórico de avaliações** | ✅ **Entra no MVP** (faz parte do design — tab fixa + recentes na home) |
| **Engine de triagem** | **Híbrido**: scoring (pesos por resposta) + red flags clínicos (override para alto risco) |
| **Idade da criança** | Persistida como `dataNascimento` (LocalDate) — front calcula em meses ou anos |
| **Notificações** | Apenas ícone visual (sem push) — abre tela "Em breve" |
| **Hospedagem** | Backend local na máquina do dev no momento da apresentação |
| **Banco de dados** | **PostgreSQL 16** em dev (via docker-compose) e em prod |
| **Testes do backend** | **Testcontainers** (sobe Postgres dockerizado por test run) |
| **Documentação de API** | **Swagger UI via Springdoc** (decisão registrada na §13) |
| **Prazo** | 4-6 semanas |
| **Time** | 5 frontend + 3 backend = 8 devs |

---

## 3. Stack técnica (resumo)

- **Frontend:** React Native + Expo (JS), `react-native-svg`, `@react-navigation/native` (bottom tabs + native stack)
- **Backend:** Java 21 + Spring Boot 3 + Maven, Spring Security, JJWT, JPA
- **DB:** PostgreSQL 16 (dev via docker-compose, prod local na apresentação)
- **Testes:** JUnit 5 + Testcontainers (Postgres em container por test run)
- **Auth:** JWT
- **Comunicação:** REST/JSON
- **Docs API:** Springdoc OpenAPI + Swagger UI
- **Repositório:** [github.com/ehoga/pji-triagem-pediatrica](https://github.com/ehoga/pji-triagem-pediatrica)

---

## 4. Design entregue

A pasta `design/` no repositório contém o protótipo completo da interface (preview web em React, com tokens e componentes prontos para portar para React Native).

**O que isso muda:**
- F00 (Setup) ganha tasks de **port** dos tokens, ícones, mascote e componentes base — em vez de criar do zero
- O time de frontend trabalha com **fidelidade visual alta** desde o início
- Reduz tempo gasto em discussões de design durante o desenvolvimento

**Componentes prontos no design:**
- Tokens de cor (primary, navy, secondary, classificação verde/amarelo/vermelho)
- Tipografia (Nunito 400-900)
- 9 ícones de sintomas + 16 ícones de UI em SVG
- Mascote em 3 moods (calm/watch/alert)
- Componentes: Card, Pill, PrimaryButton, GhostButton, ScreenHeader, ProgressBar, TabBar, Mascot
- 9 telas detalhadas: Home, Add Child, Symptoms, Quiz, Result, Orientations, History, Profile, About

---

## 5. Mapa de features

| ID | Feature | Prioridade | Esforço | Front | Back |
|---|---|---|---|---|---|
| **F00** | Setup, Infraestrutura e Port do Design | 🔴 Crítica | M+ | ✅ | ✅ |
| **F01** | Autenticação (cadastro + login + JWT) | 🔴 Crítica | G | ✅ | ✅ |
| **F02** | Cadastro e Gestão de Crianças | 🟠 Alta | M | ✅ | ✅ |
| **F03** | Catálogo de Sintomas | 🟠 Alta | P | ✅ | ✅ |
| **F04** | Engine de Triagem (scoring + red flags) | 🔴 Crítica | GG | ✅ | ✅ |
| **F05** | Resultado e Orientação | 🟠 Alta | M | ✅ | — |
| **F06** | Conteúdo Educativo (Orientações + About) | 🟡 Média | M | ✅ | — |
| **F07** | Polimento, Build e Apresentação | 🟠 Alta | M | ✅ | ✅ |
| **F08** | Testes Automatizados | 🟠 Alta (paralelo) | M | ✅ | ✅ |
| **F09** | Histórico de Avaliações ⭐ | 🟠 Alta | M | ✅ | ✅ |
| **F10** | Tela de Perfil ⭐ | 🟡 Média | P | ✅ | (mín.) |

⭐ = features adicionadas ou re-priorizadas após análise do design

**Legenda de esforço:** P = pequeno (até 2 dias), M = médio (3-5 dias), G = grande (1 semana), GG = muito grande (>1 semana, com várias frentes paralelas).

---

## 6. Dependências entre features

```
F00 (Setup + Design) ────────────────────────────────┐
   │                                                 │
   ├─→ F01 (Auth) ─→ F02 (Crianças) ─┐               │
   │                                  ├─→ F04 ─→ F05 ┤
   ├─→ F03 (Sintomas) ────────────────┘              │
   │                                                 │
   │                                F04 ─→ F09 ──────┤
   │                                                 │
   ├─→ F10 (Perfil) ─────────────────────────────────┤
   │                                                 │
   ├─→ F06 (Educativo) ──────────────────────────────┤
   │                                                 ▼
   └─→ F08 (Testes) ───── transversal              F07 (Polimento + Build)
```

**Regras de bloqueio:**
- Nada começa antes de **F00** estar pronto (variáveis de ambiente, CORS, design portado, navegação base).
- **F02** depende de **F01** (crianças vinculadas ao usuário autenticado).
- **F04** é o caminho crítico — sem o engine, nada funciona ponta a ponta.
- **F05** consome o resultado do **F04**.
- **F09** depende de **F04** porque consome a entidade `Avaliacao` já persistida.
- **F10** depende de **F02** (lista crianças) e **F01** (dados do usuário).

---

## 7. Cronograma sugerido (sprints de 1 semana)

| Sprint | Features em foco | Marco de fim de sprint |
|---|---|---|
| **S1 — Fundação** | F00 (back+front, com port do design) + início F01 | Backend e app rodando, navegação com tab bar e tokens portados, swagger acessível |
| **S2 — Identidade** | Conclusão F01 + F02 + início F03 | Login funcional, cadastro de criança (com avatar emoji), lista de sintomas |
| **S3 — Coração da app** | F04 (scoring engine + 4 fluxogramas: febre, tosse, vômitos, dor abdominal) + F03 final | Triagem ponta-a-ponta funciona para 4 sintomas |
| **S4 — Cobertura completa** | F04 (5 fluxogramas restantes + red flags) + F05 + início F09 | Todos os 9 sintomas funcionam, tela de resultado polida, histórico iniciado |
| **S5 — Refinamento** | F09 (histórico final) + F10 (perfil) + F06 (orientações + about) + F08 (testes) | Histórico, Perfil, Orientações funcionais, testes do engine |
| **S6 — Reta final** | F07 (polimento + build + ensaio) + buffer | APK gerado, demo ensaiada, bugs críticos resolvidos |

**Buffer:** S6 é parcialmente buffer. Se algum sprint atrasar, este absorve.

---

## 8. Distribuição sugerida do time

> **Importante:** distribuição é **sugestão de arranque**, não trava. Conforme o projeto avança, devs livres pegam tasks abertas.

### Backend (3 devs)

| Dev | Foco principal |
|---|---|
| **Ga (`ehoga`)** — tech lead back | F00 (Postgres+docker, CORS, CI back), F02 (CRUD criança), F09 (endpoints histórico), code review, integração, alguns fluxogramas (F04) |
| **Saniel (`MSanielMartins`)** | F01 (Spring Security + JWT + endpoints auth), F08 (testes), parte do F04 (fluxogramas + endpoints) |
| **Igor (`igorxdd`)** | F00 (Swagger), F03 (catálogo sintomas), F04 (engine de scoring + red flags + fluxogramas principais) |

### Frontend (5 devs)

| Dev | Foco principal |
|---|---|
| **Matheus (`jomatheusdev`)** — tech lead front | F00 (port tokens/componentes, axios, storage, CI front), F01-T09/T10 (AuthContext + guard), F07 build |
| **Caio (`caiomps`)** | F00-T06 (navegação tab bar), F01 (telas Login/Cadastro), F02 (telas de criança), F10 (Perfil) |
| **Jefferson (`jeffersonEzequiel`)** | F02-T03 (helper idade), F04-T15/T16 (tela de questionário com mascote/bubble), F06 (About) |
| **Pedro (`PedroLucasSCPB`)** | F00-T09 (ícones SVG), F03 (seleção sintoma), F05 (tela de resultado), F08 testes front, F09-T04 (tela Histórico) |
| **Andre (`Andrelbf41`)** | F00-T10 (Mascote), F06 (Orientações 4 categorias), F09-T05 (recentes na home) |

> Os tech leads coordenam a integração entre back e front e fazem revisão dos PRs do seu lado.

---

## 9. Critérios de aceitação do MVP

O MVP está pronto para apresentação quando:

- [ ] Usuário consegue se cadastrar e fazer login
- [ ] Usuário consegue cadastrar pelo menos uma criança (com avatar emoji)
- [ ] Os 9 sintomas estão disponíveis no catálogo
- [ ] Cada sintoma tem fluxograma de perguntas funcional com **scoring + red flags**
- [ ] Sistema gera classificação de risco correta para os 3 níveis em pelo menos 1 cenário de cada
- [ ] Tela de resultado exibe cor correta, mascote no mood adequado, chips de ação e summary
- [ ] Histórico mostra avaliações anteriores com filtro por criança e stats básicas
- [ ] Tela Perfil mostra dados do usuário e lista de crianças
- [ ] Disclaimer médico está em local visível (cards na home, em about, no resultado)
- [ ] App roda em emulador e em pelo menos 1 celular físico (Android)
- [ ] Backend roda local sem erro
- [ ] Swagger UI lista todos os endpoints com `tryItOut` funcionando
- [ ] README atualizado com instruções para clonar e rodar
- [ ] Build de APK gerada com sucesso
- [ ] Testes do engine de scoring + red flags passam (mínimo 1 caso por nível por sintoma)

---

## 10. Riscos identificados

| Risco | Impacto | Mitigação |
|---|---|---|
| **Modelagem dos 9 fluxogramas atrasa F04** | Alto — bloqueia toda a triagem | Começar F04 lado a lado com F02/F03. Usar mock no front com dados estáticos. Front pode usar o quiz "febre" do design enquanto back não entrega. |
| **Diferença de ritmo entre back e front gera bloqueio** | Médio | Definir contrato OpenAPI cedo (S1) e usar mocks. Swagger já dá schema vivo. |
| **Port do design para React Native exige `react-native-svg` e ajuste de layouts** | Médio | Tech lead front faz o port dos tokens/mascote em S1 antes de bloquear o time. |
| **Devs com pouca experiência em Spring Security** | Médio | Ga (tech lead back) faz F01 e pareia com os outros. |
| **App de saúde tem implicação legal** | Baixo (acadêmico) | Disclaimer claro em múltiplos pontos. Sem coleta de dados sensíveis. |
| **Engine híbrido pode confundir time** | Baixo | Documentar regra clara: red flag → override para HIGH. Resto = soma de scores → faixa. Testes cobrem ambos os caminhos. |

---

## 11. Backlog pós-MVP (não implementar agora)

- Recuperação de senha por email
- Edição/exclusão de avaliações antigas
- Múltiplos perfis de responsável por criança
- **Push notifications** (atualmente só ícone visual)
- Hospedagem do backend em cloud (Render/Railway/Fly)
- Versão web (PWA)
- Compartilhamento de resultado por WhatsApp
- Idioma adicional (espanhol/inglês)
- Analytics e métricas de uso
- Modo offline com sincronização
- Acessibilidade WCAG completa
- Onboarding/tutorial de primeiro uso
- Página pública de documentação (readme.io ou similar) — ver §13

---

## 12. Como usar este plano

1. Leia este `PLAN.md` para entender o todo.
2. Abra [`FEATURES.md`](./FEATURES.md) para ver as tasks da sua feature.
3. Veja a pasta `design/` no repositório para entender o visual do app.
4. Crie sua branch a partir de `dev`: `git checkout -b feature/F0X-T0Y-descricao-curta`.
5. Siga o fluxo descrito em [`CONTRIBUTING.md`](../CONTRIBUTING.md).
6. Abra PRs pequenos contra `dev` — uma task = um PR sempre que possível.

---

## 13. Decisão registrada: Swagger UI vs readme.io

Avaliamos usar [readme.io](https://readme.com) como portal de documentação da API. Após análise:

| Critério | Swagger UI (Springdoc) | readme.io |
|---|---|---|
| Custo | Grátis sempre | Free p/ 1 projeto, **Pago $250/mês** para equipe ou branching |
| Setup | 1 dependência Maven, sync automático | Conta + import + GitHub Action no CI |
| Sync com código | Automático (em runtime, `/v3/api-docs`) | Manual via `rdme` CLI ou Action |
| Vendor lock-in | Nenhum | Alto (guides Markdown ficam no readme.io) |
| Fit para faculdade / 4-6 semanas | ✅ Adequado | ❌ Overkill |

**Decisão:** usar **Swagger UI via Springdoc** no MVP. A doc fica em `localhost:8080/swagger-ui.html`, sincronizada automaticamente com o código Java.

**Plano B (apresentação polida):** se sobrar tempo na S5/S6, exportar `openapi.json` do Springdoc e jogar no plano Free do readme.io só para ter um link visualmente bonito na apresentação. Vai como tarefa opcional em F07.

Fontes da pesquisa: [readme.com/pricing](https://readme.com/pricing), [docs.readme.com](https://docs.readme.com/main/docs/github-actions-openapi-example), [springdoc.org](https://springdoc.org/).

---

**Última atualização:** 2026-05-04
**Autores:** Time PJI Triagem Pediátrica
