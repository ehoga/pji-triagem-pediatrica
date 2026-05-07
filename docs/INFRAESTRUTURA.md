# Infraestrutura, Deploy e CI/CD — desenho recomendado

> Documento de arquitetura de infraestrutura do **PediTriagem**. Cobre topologia de
> deploy, comparação de opções, pipeline GitHub Actions e estratégia de evolução.
> Para o passo-a-passo de provisionamento, ver [`infra/SETUP.md`](../infra/SETUP.md).
> Para estratégia de migrations de banco, ver [`MIGRATIONS.md`](./MIGRATIONS.md).

## Sumário

1. [Antes de tudo: o frontend não vai pra servidor](#1-antes-de-tudo-o-frontend-não-vai-pra-servidor)
2. [As opções reais e o que cada uma ensina](#2-as-opções-reais-e-o-que-cada-uma-ensina)
3. [Recomendação: VM única + Docker Compose](#3-recomendação-vm-única--docker-compose)
4. [Topologia detalhada](#4-topologia-detalhada)
5. [Pipeline GitHub Actions](#5-pipeline-github-actions)
6. [Estratégia de evolução em 5 etapas](#6-estratégia-de-evolução-em-5-etapas)
7. [Comparação de provedores](#7-comparação-de-provedores)
8. [Custo realista](#8-custo-realista)
9. [O que NÃO fazer](#9-o-que-não-fazer)
10. [Resumo executivo](#10-resumo-executivo)

---

## 1. Antes de tudo: o frontend não vai pra servidor

Esse é o ponto que muda toda a discussão. Vamos olhar o que cada parte do projeto realmente é:

| Componente | O que é | Onde "vive" | Precisa de servidor? |
|---|---|---|---|
| **App React Native (Expo)** | Binário nativo iOS | No iPhone do usuário, instalado via **TestFlight** | Não |
| **Backend Spring Boot** | API HTTP que serve JSON | Em algum lugar com IP público + HTTPS | Sim |
| **PostgreSQL** | Banco de dados | Acessível pelo backend | Sim |
| **Swagger UI** | Doc da API | Servida pelo próprio backend (`/swagger-ui.html`) | (já vai junto) |
| **Design preview** (`design/PediTriagem.html`) | HTML estático demo | Pode ser GitHub Pages | Opcional |
| **Expo Web** (opcional) | Versão web do app | Vercel/Netlify (free) | Opcional |

**Insight 1:** "Deploy do frontend" para esse projeto significa **build do app + upload pra App Store Connect (TestFlight)**. Não é "subir num servidor web". Isso muda tudo — não há o problema clássico "front separado do back".

**Insight 2:** O que precisa rodar 24/7 num servidor é só **API + DB**. Esse é o sistema "online" do projeto.

A pergunta correta passa a ser: **"Como hospedar a API + o banco de uma forma que ensine pipeline, deploy e infra?"**

---

## 2. As opções reais e o que cada uma ensina

| Opção | Topologia | O que aprende | Custo | Esforço |
|---|---|---|---|---|
| **A — Tudo numa VM única, processos nativos** | 1 VM, Postgres como serviço linux, java -jar como systemd | Linux básico, systemd, ports, firewall | Baixo | Alto |
| **B — VM única + Docker Compose** ⭐ | 1 VM, `docker-compose up` com api + postgres + nginx | Docker, redes container, reverse proxy, HTTPS, secrets | Baixo | Médio |
| **C — VM separada por componente** | 1 VM api + 1 VM db | Networking entre hosts, segurança de rede | Médio | Alto |
| **D — Plataforma PaaS (Render/Railway/Fly.io)** | "git push e deploy" | Workflow moderno, autoscaling | Free tier ou ~$5/mês | Baixo |
| **E — Cloud puro (AWS/GCP)** | EC2 + RDS, ou ECS, ou Cloud Run | Cloud "real" (currículo) | Free tier limitado | Muito alto |
| **F — Híbrido: Postgres gerenciado + API em VM** | API numa VM + Postgres no Neon/Supabase free | Managed services + VM, backups grátis | Baixo | Médio |

---

## 3. Recomendação: VM única + Docker Compose

A **Opção B** é o doce ponto pedagógico para esse projeto.

### Por quê

Vocês querem **aprender pipeline, deploy e infra**. Esses três tópicos juntos são MUITO. Se escolherem AWS/GCP, vão passar 2 semanas brigando com IAM, VPC e security groups antes de fazer o primeiro deploy. Se escolherem Render/Railway, vão clicar dois botões e nunca entender o que tá embaixo.

A Opção B é **a quantidade certa de "tudo de uma vez"** para aprender:

- **Docker e Docker Compose** → padrão da indústria
- **Reverse proxy** (Nginx ou Caddy) → todo backend tem isso na frente
- **HTTPS automático** (Let's Encrypt) → segurança real, gratuito
- **Secrets management** (GitHub Secrets + .env file) → como NÃO commitar senha
- **systemd ou Docker restart policy** → o serviço sobe sozinho se cair
- **GitHub Actions deployando via SSH** → CI/CD ponta a ponta
- **Backup de Postgres** → primeiro hábito sério de DBA
- **Logs e métricas básicas** → observabilidade

E vocês podem montar tudo isso **em 1 VM de R$30/mês** ou no free tier da Oracle Cloud (4 vCPU + 24GB RAM ARM grátis para sempre).

---

## 4. Topologia detalhada

```
                 ┌──────────────────────────────────────────────────┐
                 │                  GitHub                          │
                 │  ┌─────────┐                                     │
                 │  │  repo   │  ─── PR ───►  Actions: build+test   │
                 │  │ (dev)   │                                     │
                 │  └────┬────┘                                     │
                 │       │ merge to dev                             │
                 │       ▼                                          │
                 │  Actions: deploy (SSH ou docker registry push)   │
                 └────────────────────────┬─────────────────────────┘
                                          │
                                          ▼ (ssh + docker compose pull/up)
   ┌────────────────────────────────────────────────────────────────────────┐
   │                       VPS único (Hetzner / DigitalOcean / Oracle)      │
   │                                                                        │
   │    ┌─────────────────────────────────────────────────────────────┐     │
   │    │          docker-compose.yml em produção                     │     │
   │    │                                                             │     │
   │    │  ┌───────────────┐    ┌───────────────┐   ┌─────────────┐   │     │
   │    │  │   caddy       │    │   triagem-api │   │  postgres   │   │     │
   │    │  │   (proxy +    │───►│   spring boot │──►│      16     │   │     │
   │    │  │    HTTPS)     │    │   (java jar)  │   │  (volume    │   │     │
   │    │  │   :443/:80    │    │   :8080       │   │  persistent)│   │     │
   │    │  └───────┬───────┘    └───────────────┘   └─────────────┘   │     │
   │    └──────────┼──────────────────────────────────────────────────┘     │
   │               │                                                        │
   └───────────────┼────────────────────────────────────────────────────────┘
                   │
                   │ HTTPS (api.peditriagem.exemplo.com)
                   │
                   ▼
        ┌──────────────────────┐
        │   App no iPhone do   │
        │     responsável      │
        │  (instalado via      │
        │     TestFlight)      │
        └──────────────────────┘
```

### 4.1 Componentes explicados

**Caddy** (em vez de Nginx): por quê? Porque **Caddy tira HTTPS automático do Let's Encrypt sem você escrever uma linha de config**. Nginx + certbot exige 30min de tutorial. Caddy é literalmente:

```caddy
api.peditriagem.exemplo.com {
    reverse_proxy triagem-api:8080
}
```

E pronto. HTTPS funciona, renova sozinho, sem você pensar. Para aprendizado, recomendo Caddy. Quando virarem profissionais e precisarem de configs avançadas, migram para Nginx.

**Spring Boot como container Java**: cria-se um `Dockerfile` no `backend/` que faz o build do jar e roda. Algo como:

```dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline
COPY src src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre
COPY --from=build /app/target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

**Postgres com volume persistente**: o volume é nomeado (`postgres_data`), então mesmo que destruam o container, os dados ficam. **Esse é o pulo do gato** que separa quem entendeu Docker de quem não entendeu.

**Network interno**: api e postgres conversam pelo nome do serviço (`postgres:5432`), porque docker-compose cria uma rede automática. **Postgres NÃO expõe porta para fora** — só a api fala com ele. Defesa em profundidade.

---

## 5. Pipeline GitHub Actions

### 5.1 Mapa dos workflows

```
.github/workflows/
├── backend-ci.yml         ← roda em PR (já planejado em F00-T05)
├── frontend-ci.yml        ← roda em PR (já planejado em F00-T13)
├── deploy-staging.yml     ← roda em merge para `dev`
├── deploy-prod.yml        ← roda em release tag (v1.0.0)
└── db-backup.yml          ← roda em cron (1x/dia)
```

### 5.2 Os ambientes

| Ambiente | URL | Quando atualiza | Quem aprova |
|---|---|---|---|
| **dev** (local) | `localhost:8080` | toda hora, na máquina do dev | ninguém |
| **staging** | `api-staging.peditriagem.exemplo.com` | a cada merge em `dev` | automático |
| **prod** | `api.peditriagem.exemplo.com` | a cada tag `vX.Y.Z` na `main` | manual (1 aprovação) |

**Insight:** essa hierarquia (dev → staging → prod) é o padrão que vocês vão encontrar em qualquer empresa. Ter os 3 reflete o fluxo de branches já adotado (`feature/...` → `dev` → `main`).

Para um MVP acadêmico, vocês podem **começar só com prod**, e adicionar staging quando incomodar (ex: quando alguém colocou bug em prod e não foi pego).

### 5.3 O workflow de deploy (esqueleto)

```yaml
# .github/workflows/deploy-prod.yml
name: Deploy Production

on:
  push:
    tags:
      - 'v*'

jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: production   # exige aprovação manual no GitHub
    steps:
      - uses: actions/checkout@v4

      - name: Build Docker image
        run: |
          docker build -t ghcr.io/${{ github.repository }}/triagem-api:${{ github.ref_name }} backend/

      - name: Login to GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Push image
        run: |
          docker push ghcr.io/${{ github.repository }}/triagem-api:${{ github.ref_name }}
          docker tag ghcr.io/${{ github.repository }}/triagem-api:${{ github.ref_name }} \
                     ghcr.io/${{ github.repository }}/triagem-api:latest
          docker push ghcr.io/${{ github.repository }}/triagem-api:latest

      - name: Deploy via SSH
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          script: |
            cd /opt/peditriagem
            docker compose pull
            docker compose up -d
            docker image prune -f
```

**O que isso ensina:**

1. **Container registry** (`ghcr.io` é grátis, vinculado ao GitHub)
2. **Tagging por versão** (`v1.0.0`) — não é hash de commit aleatório
3. **Aprovação manual** (`environment: production`) — alguém precisa clicar antes de prod
4. **Secrets** (chave SSH **nunca** no código)
5. **Deploy via SSH** — simples, robusto, fácil de debugar

### 5.4 Coisas que parecem detalhe e ensinam muito

**Healthcheck na pipeline**: depois do `docker compose up`, fazer `curl https://api.../health` e abortar se 200 não voltar em 30s. Isso evita publicar uma versão quebrada.

**Rollback automático**: se healthcheck falha, voltar para a tag anterior. Em script bash isso é trivial e ensina o conceito.

**Migrations de banco**: o projeto usa JPA `ddl-auto=update`. Para prod, isso é **arriscado** (pode dropar coluna sem aviso). Migrar para **Flyway** é uma das melhores práticas que vocês podem aprender. Detalhes em [`MIGRATIONS.md`](./MIGRATIONS.md).

---

## 6. Estratégia de evolução em 5 etapas

```
┌────────────────────────────────────────────────────────────────┐
│ Etapa 1 — Hello server (1 final de semana)                     │
│   - Provisione 1 VM (Hetzner ou Oracle free tier)              │
│   - SSH manual, instala docker, sobe docker-compose            │
│   - HTTP simples (sem domínio ainda)                           │
│   - Aprende: Linux, Docker, networking básico                  │
└────────────────────────────────────────────────────────────────┘
                                ▼
┌────────────────────────────────────────────────────────────────┐
│ Etapa 2 — Domínio + HTTPS (1 dia)                              │
│   - Compra ou subdomínio grátis (DuckDNS, Cloudflare)          │
│   - Caddy com HTTPS automático                                 │
│   - Aprende: DNS, certificados, reverse proxy                  │
└────────────────────────────────────────────────────────────────┘
                                ▼
┌────────────────────────────────────────────────────────────────┐
│ Etapa 3 — Pipeline GitHub (2-3 dias)                           │
│   - workflow de build/test em PR                               │
│   - workflow de deploy em merge/tag                            │
│   - Secrets configurados                                       │
│   - Aprende: CI/CD, container registry, SSH em CI              │
└────────────────────────────────────────────────────────────────┘
                                ▼
┌────────────────────────────────────────────────────────────────┐
│ Etapa 4 — Operacional (1 semana)                               │
│   - Backup automático do Postgres (cron + S3 ou Backblaze B2)  │
│   - Logs centralizados (Loki + Grafana, ou só `docker logs`)   │
│   - Healthcheck e auto-restart                                 │
│   - Migrations com Flyway                                      │
│   - Aprende: ops, observabilidade, schema migrations           │
└────────────────────────────────────────────────────────────────┘
                                ▼
┌────────────────────────────────────────────────────────────────┐
│ Etapa 5 — Profissionalização (futuro)                          │
│   - Postgres gerenciado (Neon free tier, Supabase, RDS)        │
│   - Múltiplas instâncias da API + load balancer                │
│   - Métricas (Prometheus + Grafana)                            │
│   - Tracing (OpenTelemetry)                                    │
│   - Container orchestration (Docker Swarm ou k3s)              │
│   - Aprende: produção real                                     │
└────────────────────────────────────────────────────────────────┘
```

**Por que essa ordem?** Cada etapa **resolve um problema que a anterior fez você sentir**. Você não aprende backup quando ainda não perdeu dado. Você não aprende logs centralizados quando o `docker logs` ainda dá conta. Aprenda por **dor**, não por hype.

---

## 7. Comparação de provedores

| Provedor | Custo | Vantagem | Desvantagem |
|---|---|---|---|
| **Oracle Cloud Always Free** | $0 sempre | 4 vCPU ARM + 24 GB RAM grátis pra sempre | Onboarding chato, ARM (alguma incompatibilidade), pode "limpar" conta inativa |
| **Hetzner Cloud** | €4-5/mês | Melhor custo-benefício do mundo, infra alemã sólida | Precisa cartão internacional |
| **DigitalOcean** | $6/mês | UI muito boa, ótimos tutoriais | Mais caro que Hetzner |
| **AWS Free Tier** | $0 (1 ano) → $$$ | Currículo, ecossistema | Risco de bill shock se errar config |
| **Render / Railway / Fly.io** | Free tier limitado | Deploy via git, sem SSH | Aprende menos infra "raw" |
| **Neon (Postgres)** | Free tier generoso | DB gerenciado, branching de schema | Limite de quanto fica ativo |
| **Supabase** | Free tier | Postgres + auth + storage | Atrai uso fora do escopo |

**Recomendação**: comece com **Hetzner** (€4/mês) ou **Oracle Always Free** (gratuito mas mais setup). Esses dois te dão uma VM "de verdade" para aprender.

Em paralelo, considere **Neon** para o Postgres só **depois** que entenderem como rodar Postgres em container — a ordem importa para aprender.

---

## 8. Custo realista

Hetzner como base:

| Item | Custo/mês |
|---|---|
| VM Hetzner CX22 (2 vCPU, 4GB RAM, 40GB SSD) | €4 (~R$25) |
| Domínio `.com.br` | R$40/ano (~R$3/mês) |
| Domínio `.dev` ou `.app` | $12/ano (~R$5/mês) |
| Backup automático Hetzner (opcional) | €0,80 (~R$5) |
| Object storage para backups DB (Backblaze B2) | $0 até 10GB |
| GitHub Actions, Container Registry | $0 (até 2000 min/mês) |
| **Total** | **~R$33-38/mês** |

Divide por 8 devs = R$4-5 por dev/mês. **Isso é menos que um café**. E vocês aprendem o que valeria milhares de reais em curso.

Se forem para Oracle Free Tier: **R$3-5/mês só do domínio**.

---

## 9. O que NÃO fazer

1. **Subir Kubernetes para um app de 8 devs em MVP.** Você vai gastar 3 semanas configurando k8s e zero entregando feature. Quando vocês precisarem de k8s, vão perceber. Não é agora.
2. **Microserviços.** Mesma coisa — está lindo no diagrama, mata o time na prática.
3. **Multi-cloud "para resiliência".** Vocês não têm SLA. Foco.
4. **Configurar tudo manualmente sem versionar.** Scripts de provisionamento (Terraform, Ansible) parecem overkill, mas mesmo um `setup.sh` em bash no repo já te salva quando alguém precisa subir do zero. Comece simples mas **versione**.
5. **Senhas em arquivos `.env` commitados.** Use `.env.example` no repo, `.env` no `.gitignore`, valores reais no GitHub Secrets.
6. **Acesso SSH com senha.** Só por chave. E só pela máquina dos tech leads inicialmente.
7. **Postgres exposto na internet.** Porta 5432 fechada no firewall. Backend conecta via rede interna do docker. Para emergência, túnel SSH.
8. **`ddl-auto=update` em produção.** Migre para Flyway antes de subir prod.
9. **Logar dados sensíveis.** Email, senha (mesmo hash), token JWT — nada disso em logs. Em saúde, isso vira problema legal rápido.
10. **Fazer deploy direto de `dev` para prod sem release.** Use tags semver (`v1.0.0`). Vocês ganham rastreabilidade e rollback fácil.

---

## 10. Resumo executivo

**Topologia:** 1 VPS Hetzner (€4/mês) ou Oracle Free Tier rodando `docker-compose` com 3 containers: **caddy** (proxy + HTTPS), **triagem-api** (Spring Boot), **postgres**.

**Frontend:** sem servidor — distribuição via TestFlight como já planejado. Opcionalmente subir Expo Web grátis no Vercel se quiserem demo via browser.

**Pipeline:** GitHub Actions com 4 workflows — backend-ci (PR), frontend-ci (PR), deploy-staging (merge em `dev`), deploy-prod (tag em `main`). Imagens Docker armazenadas no GitHub Container Registry (grátis).

**Domínio:** `peditriagem.com.br` (R$40/ano) com subdomínios `api.peditriagem.com.br` (prod) e `api-staging.peditriagem.com.br` (staging).

**Por quê essa escolha:** ensina **a maior superfície de aprendizado por menor complexidade**. Vocês saem do projeto sabendo Docker, Linux, reverse proxy, HTTPS, CI/CD, secrets, registry, backups, healthchecks. Esse stack cobre 80% do que se faz em qualquer empresa.

**Como começar:** crie uma fase nova no roadmap — algo como **F11 — Infra de Produção e CI/CD** — com tasks paralelas a F00. Pode rodar em paralelo com o desenvolvimento (não bloqueia features). Idealmente Ga + Matheus (os dois tech leads) lideram, com 1-2 devs interessados em aprender DevOps em par.

**Próximos passos práticos:**

- Para o passo-a-passo de provisionamento → [`infra/SETUP.md`](../infra/SETUP.md)
- Para a estratégia de migrations de banco → [`MIGRATIONS.md`](./MIGRATIONS.md)

---

**Última atualização:** 2026-05-06
**Autor:** Análise arquitetural sênior (mentoria)
