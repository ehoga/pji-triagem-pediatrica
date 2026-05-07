# Setup de Infraestrutura — passo a passo

> Guia operacional para provisionar o ambiente de produção do **PediTriagem** do zero.
> Para a justificativa arquitetural ("por que assim?"), ver [`docs/INFRAESTRUTURA.md`](../docs/INFRAESTRUTURA.md).
> Para a estratégia de migrations, ver [`docs/MIGRATIONS.md`](../docs/MIGRATIONS.md).

**Estimativa de tempo total (primeira vez):** 4-6 horas, distribuídas em 1-2 dias.

## Sumário

1. [Pré-requisitos](#1-pré-requisitos)
2. [Provisionar a VM](#2-provisionar-a-vm)
3. [Hardening inicial (SSH + firewall)](#3-hardening-inicial-ssh--firewall)
4. [Instalar Docker](#4-instalar-docker)
5. [Configurar DNS](#5-configurar-dns)
6. [Estrutura de pastas no servidor](#6-estrutura-de-pastas-no-servidor)
7. [Arquivos de configuração](#7-arquivos-de-configuração)
8. [Primeiro deploy manual](#8-primeiro-deploy-manual)
9. [Configurar GitHub Actions](#9-configurar-github-actions)
10. [Backup automatizado do Postgres](#10-backup-automatizado-do-postgres)
11. [Operações comuns no dia-a-dia](#11-operações-comuns-no-dia-a-dia)
12. [Troubleshooting](#12-troubleshooting)
13. [Checklist final](#13-checklist-final)

---

## 1. Pré-requisitos

Antes de começar, providencie:

- [ ] **Conta num provedor de cloud** — recomendado Hetzner (€4/mês) ou Oracle Cloud (free tier). Ver comparação em [`docs/INFRAESTRUTURA.md` §7](../docs/INFRAESTRUTURA.md#7-comparação-de-provedores).
- [ ] **Domínio próprio** — comprar `.com.br` no Registro.br (R$40/ano) ou pegar subdomínio grátis no DuckDNS.
- [ ] **Cartão internacional** — só se for Hetzner (Oracle aceita brasileiros sem isso).
- [ ] **Chave SSH local gerada** na máquina de quem vai administrar:
  ```bash
  # se ainda não tem
  ssh-keygen -t ed25519 -C "seu-email@exemplo.com"
  # gera ~/.ssh/id_ed25519 (privada) e ~/.ssh/id_ed25519.pub (pública)
  ```
- [ ] **Conta GitHub com permissão de admin no repo** (para configurar secrets e environments).

---

## 2. Provisionar a VM

### 2.1 Escolha de imagem

- **Sistema:** Ubuntu 24.04 LTS
- **Tamanho mínimo:** 2 vCPU, 4 GB RAM, 40 GB SSD
  - No Hetzner: tipo **CX22** (€4/mês)
  - Na Oracle: **VM.Standard.A1.Flex** com 2 OCPU + 12 GB (free tier)
- **Localização:** mais próxima do Brasil possível
  - Hetzner: nenhuma região no Brasil — usar Helsinki (HEL1) ou Ashburn (US-east)
  - Oracle: São Paulo (`sa-saopaulo-1`)
- **Chave SSH:** cole a sua **pública** (`~/.ssh/id_ed25519.pub`) na hora da criação
- **Firewall do provedor:** se houver opção, libere apenas 22 (SSH), 80 (HTTP) e 443 (HTTPS)

### 2.2 Primeiro acesso

Quando a VM ficar pronta, anote o **IP público** e teste o SSH:

```bash
ssh root@<IP_DA_VM>
# se aparecer prompt de senha, sua chave não foi configurada — recomeça
# se entrar direto, ✓
```

A partir daqui, todos os comandos rodam **dentro da VM** (após o `ssh`), exceto onde estiver explicitamente marcado **[local]**.

---

## 3. Hardening inicial (SSH + firewall)

A VM acabou de ser criada e está exposta na internet. **Antes de instalar qualquer coisa**, feche o que pode ser fechado.

### 3.1 Atualizar o sistema

```bash
apt update && apt upgrade -y
apt install -y curl ufw fail2ban unattended-upgrades
```

### 3.2 Criar usuário não-root

Rodar serviço como `root` é prática péssima. Crie um usuário dedicado:

```bash
adduser deploy            # responda as perguntas (senha forte!)
usermod -aG sudo deploy   # dá sudo ao deploy
mkdir -p /home/deploy/.ssh
cp ~/.ssh/authorized_keys /home/deploy/.ssh/authorized_keys
chown -R deploy:deploy /home/deploy/.ssh
chmod 700 /home/deploy/.ssh
chmod 600 /home/deploy/.ssh/authorized_keys
```

Teste em outro terminal: `ssh deploy@<IP>`. Se entrar, ✓.

### 3.3 Desabilitar login root via SSH

```bash
# edite /etc/ssh/sshd_config (ou /etc/ssh/sshd_config.d/*.conf)
# garanta as seguintes linhas:
PermitRootLogin no
PasswordAuthentication no
PubkeyAuthentication yes

# recarregue
systemctl restart ssh
```

### 3.4 Firewall (UFW)

```bash
ufw default deny incoming
ufw default allow outgoing
ufw allow 22/tcp     # SSH
ufw allow 80/tcp     # HTTP (redirect to HTTPS)
ufw allow 443/tcp    # HTTPS
ufw enable
ufw status verbose   # confirme
```

**Importante:** porta 5432 (Postgres) **NÃO** é liberada. Postgres só fala com o backend pela rede interna do Docker.

### 3.5 Fail2ban (opcional, mas recomendado)

```bash
systemctl enable --now fail2ban
# Default já protege SSH. Para ver tentativas bloqueadas:
fail2ban-client status sshd
```

### 3.6 Atualizações automáticas de segurança

```bash
dpkg-reconfigure --priority=low unattended-upgrades
# responda "Yes" para instalar updates de segurança automaticamente
```

---

## 4. Instalar Docker

A partir daqui, **logue como `deploy`** (não como `root`):

```bash
exit                                        # sai do root
ssh deploy@<IP>                             # entra como deploy
```

Instale Docker (script oficial):

```bash
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker deploy

# saia e reentre para o grupo docker valer
exit
ssh deploy@<IP>

# teste
docker run --rm hello-world
docker compose version
```

Você deve ver `Docker Compose version v2.x.x`. Se não, atualize.

---

## 5. Configurar DNS

No painel do provedor de domínio (Registro.br, Cloudflare, etc.), crie 2 registros tipo **A**:

| Subdomínio | Tipo | Valor | TTL |
|---|---|---|---|
| `api` (vira `api.peditriagem.com.br`) | A | `<IP_DA_VM>` | 300 |
| `api-staging` (opcional) | A | `<IP_DA_VM>` | 300 |

Espere 5-15 min para propagação. Teste:

```bash
# [local]
dig api.peditriagem.com.br +short
# deve retornar o IP da VM
```

**Dica:** se ainda não tem domínio, use DuckDNS:

1. Acesse `duckdns.org`, faça login com GitHub
2. Crie subdomínio `peditriagem.duckdns.org` apontando pro IP da VM
3. Vai funcionar igual, com URL mais "feia" mas funcional

---

## 6. Estrutura de pastas no servidor

```bash
sudo mkdir -p /opt/peditriagem
sudo chown deploy:deploy /opt/peditriagem
cd /opt/peditriagem

mkdir -p backups secrets caddy
```

Estrutura final:

```
/opt/peditriagem/
├── docker-compose.yml      ← orquestra os 3 containers
├── .env                    ← secrets (NÃO commitar)
├── caddy/
│   └── Caddyfile           ← config do reverse proxy
├── backups/                ← dumps do Postgres caem aqui
└── secrets/                ← qualquer chave/cert manual
```

---

## 7. Arquivos de configuração

### 7.1 `/opt/peditriagem/docker-compose.yml`

```yaml
services:
  caddy:
    image: caddy:2-alpine
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./caddy/Caddyfile:/etc/caddy/Caddyfile:ro
      - caddy_data:/data
      - caddy_config:/config
    depends_on:
      - triagem-api

  triagem-api:
    image: ghcr.io/SEU_USER/pji-triagem-pediatrica/triagem-api:latest
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: triagem
      DB_USER: triagem
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION_MS: 86400000
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 5s
      retries: 3
      start_period: 60s

  postgres:
    image: postgres:16-alpine
    restart: unless-stopped
    environment:
      POSTGRES_DB: triagem
      POSTGRES_USER: triagem
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./backups:/backups
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U triagem -d triagem"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
  caddy_data:
  caddy_config:
```

**Pontos importantes:**

- `postgres` **não publica porta** (não tem `ports:` mapeado). Só fala dentro da rede docker.
- `triagem-api` **não publica porta** tampouco — só Caddy fala com ela.
- Apenas Caddy expõe 80 e 443 para a internet.
- `depends_on` com `service_healthy` garante que API só sobe quando Postgres respondeu.
- `restart: unless-stopped` faz os containers subirem sozinhos depois de reboot.

### 7.2 `/opt/peditriagem/caddy/Caddyfile`

```caddy
api.peditriagem.com.br {
    reverse_proxy triagem-api:8080

    # Logs (opcional)
    log {
        output file /data/access.log
        format json
    }
}

# Redirecionar HTTP automaticamente — Caddy já faz isso por padrão
```

Substitua `api.peditriagem.com.br` pelo seu domínio real. Caddy vai pegar o certificado HTTPS automaticamente do Let's Encrypt na primeira execução.

### 7.3 `/opt/peditriagem/.env`

**NUNCA** commitar esse arquivo. Crie no servidor com permissões restritas:

```bash
cat > /opt/peditriagem/.env <<'EOF'
DB_PASSWORD=GERE_UMA_SENHA_FORTE_AQUI
JWT_SECRET=GERE_UMA_CHAVE_DE_64_CARACTERES_AQUI
EOF

chmod 600 /opt/peditriagem/.env
```

Para gerar valores fortes:

```bash
# senha do DB (32 chars)
openssl rand -base64 32

# JWT secret (64 chars)
openssl rand -base64 64
```

### 7.4 `backend/Dockerfile` (no repo, no Git)

Esse arquivo vai versionado no repo. Crie em `backend/Dockerfile`:

```dockerfile
# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Por que multi-stage?** A imagem final usa só JRE (~200MB), não JDK (~500MB). Mais leve, menos superfície de ataque.

**Por que `curl` no runtime?** Para o `healthcheck` do docker-compose funcionar.

---

## 8. Primeiro deploy manual

Antes de configurar a pipeline, faça **um deploy manual completo** para validar.

### 8.1 [Local] Build e push da imagem

```bash
# logue no GHCR localmente
echo "$GITHUB_PAT" | docker login ghcr.io -u SEU_USER --password-stdin
# (gere um Personal Access Token com escopo write:packages em github.com/settings/tokens)

# build
cd backend
docker build -t ghcr.io/SEU_USER/pji-triagem-pediatrica/triagem-api:v0.1.0 .
docker tag ghcr.io/SEU_USER/pji-triagem-pediatrica/triagem-api:v0.1.0 \
           ghcr.io/SEU_USER/pji-triagem-pediatrica/triagem-api:latest

# push
docker push ghcr.io/SEU_USER/pji-triagem-pediatrica/triagem-api:v0.1.0
docker push ghcr.io/SEU_USER/pji-triagem-pediatrica/triagem-api:latest
```

### 8.2 [Servidor] Login no GHCR e pull

A imagem está marcada como **privada** por padrão. O servidor precisa logar:

```bash
echo "$GITHUB_PAT" | docker login ghcr.io -u SEU_USER --password-stdin
```

(use o mesmo PAT, ou crie um separado só para o servidor com escopo `read:packages`)

**Dica:** depois você pode tornar a imagem pública nas settings do package no GitHub e dispensar esse login. Para um projeto acadêmico OSS faz sentido.

### 8.3 [Servidor] Subir tudo

```bash
cd /opt/peditriagem
docker compose pull
docker compose up -d
docker compose ps     # confirme que os 3 estão "running" e "healthy"
```

### 8.4 Validar

```bash
# de qualquer lugar
curl https://api.peditriagem.com.br/health
# deve retornar 200 com {"status":"ok",...}

curl https://api.peditriagem.com.br/swagger-ui.html
# deve carregar
```

Se HTTPS não funcionar de primeira, espere 1-2 min — Caddy demora para emitir o cert na primeira vez. Se persistir, ver §12 (troubleshooting).

---

## 9. Configurar GitHub Actions

### 9.1 Secrets no GitHub

Vá em **Settings → Secrets and variables → Actions** do repo e adicione:

| Nome | Valor |
|---|---|
| `SERVER_HOST` | IP da VM (ex: `5.161.42.10`) |
| `SERVER_USER` | `deploy` |
| `SERVER_SSH_KEY` | conteúdo da **chave privada** SSH (a `~/.ssh/id_ed25519`, NÃO a `.pub`) |

**Importante:** a chave SSH usada pelo CI deve ser **diferente** da chave pessoal dos devs. Gere uma específica:

```bash
# [local]
ssh-keygen -t ed25519 -f ~/.ssh/peditriagem_ci -C "ci@peditriagem"
# gera 2 arquivos: peditriagem_ci (privada) e peditriagem_ci.pub (pública)

# adicione a pública no servidor
ssh-copy-id -i ~/.ssh/peditriagem_ci.pub deploy@<IP>

# cole o conteúdo de peditriagem_ci (PRIVADA) no secret SERVER_SSH_KEY
cat ~/.ssh/peditriagem_ci
```

### 9.2 Environment de produção

Em **Settings → Environments**, crie um environment chamado **`production`** e marque **"Required reviewers"** com pelo menos 1 pessoa. Isso garante que toda deploy de prod precisa de aprovação manual no GitHub.

### 9.3 Workflow de deploy

Crie `.github/workflows/deploy-prod.yml` no repo:

```yaml
name: Deploy Production

on:
  push:
    tags:
      - 'v*'

jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: production
    permissions:
      contents: read
      packages: write
    steps:
      - uses: actions/checkout@v4

      - name: Login to GHCR
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build and push image
        uses: docker/build-push-action@v5
        with:
          context: ./backend
          push: true
          tags: |
            ghcr.io/${{ github.repository }}/triagem-api:${{ github.ref_name }}
            ghcr.io/${{ github.repository }}/triagem-api:latest

      - name: Deploy via SSH
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          script: |
            cd /opt/peditriagem
            docker compose pull
            docker compose up -d --remove-orphans
            docker image prune -f

      - name: Healthcheck
        run: |
          sleep 30
          for i in 1 2 3 4 5; do
            if curl -fsS https://api.peditriagem.com.br/health; then
              echo "✓ Healthy"
              exit 0
            fi
            echo "Try $i/5 failed, waiting 10s..."
            sleep 10
          done
          echo "✗ Healthcheck failed after 5 tries"
          exit 1
```

### 9.4 Disparar o primeiro deploy

```bash
# [local]
git tag v0.1.0
git push origin v0.1.0
```

Acompanhe em **Actions** no GitHub. Após aprovação manual, deve build → push → deploy → healthcheck.

---

## 10. Backup automatizado do Postgres

Sem isso, qualquer perda de VM ou exclusão acidental destrói os dados. **Faça antes de subir prod de verdade.**

### 10.1 Script de backup local

Crie `/opt/peditriagem/backup.sh` na VM:

```bash
#!/bin/bash
set -e

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="/opt/peditriagem/backups/triagem_${TIMESTAMP}.sql.gz"

cd /opt/peditriagem

# dump comprimido
docker compose exec -T postgres pg_dump -U triagem triagem | gzip > "$BACKUP_FILE"

# manter só últimos 14 dias
find /opt/peditriagem/backups -name "triagem_*.sql.gz" -mtime +14 -delete

echo "Backup OK: $BACKUP_FILE ($(du -h $BACKUP_FILE | cut -f1))"
```

```bash
chmod +x /opt/peditriagem/backup.sh
# teste uma vez
/opt/peditriagem/backup.sh
ls -lh /opt/peditriagem/backups/
```

### 10.2 Cron diário

```bash
crontab -e
# adicione:
0 3 * * * /opt/peditriagem/backup.sh >> /opt/peditriagem/backups/backup.log 2>&1
```

(rodará às 3h da manhã todo dia)

### 10.3 Off-site (opcional, mas recomendado)

Backups locais não protegem contra perda da VM. Para enviar para Backblaze B2 (free tier 10GB):

```bash
# instale rclone
curl https://rclone.org/install.sh | sudo bash

# configure (interativo)
rclone config
# escolha: b2 → cole credenciais B2

# adicione no script de backup:
rclone copy "$BACKUP_FILE" b2:peditriagem-backups/
```

### 10.4 Restaurar

```bash
# para recuperar de um backup:
gunzip < /opt/peditriagem/backups/triagem_20260506_030000.sql.gz | \
  docker compose exec -T postgres psql -U triagem triagem
```

**Faça um drill de restore pelo menos uma vez.** Backup que você nunca testou restaurar **provavelmente não funciona**.

---

## 11. Operações comuns no dia-a-dia

### Ver logs em tempo real

```bash
cd /opt/peditriagem
docker compose logs -f --tail=100             # todos os serviços
docker compose logs -f triagem-api            # só a API
```

### Reiniciar um serviço

```bash
docker compose restart triagem-api
```

### Atualizar manualmente (sem CI)

```bash
docker compose pull triagem-api
docker compose up -d triagem-api
```

### Rollback rápido

```bash
# editar docker-compose.yml temporariamente para usar tag específica
# image: ghcr.io/.../triagem-api:v0.1.0   <- versão anterior
docker compose pull
docker compose up -d
```

Ou usar a tag específica direto:

```bash
docker compose stop triagem-api
docker run -d --name triagem-api-rollback \
  --network peditriagem_default \
  --env-file .env \
  ghcr.io/.../triagem-api:v0.0.9
```

### Acessar o Postgres direto

```bash
docker compose exec postgres psql -U triagem triagem
# dentro: \dt para listar tabelas, \q para sair
```

### Ver consumo de recursos

```bash
docker stats              # CPU, RAM, network por container
df -h /var/lib/docker     # espaço em disco
free -h                   # RAM da VM
```

### Atualizar a VM

```bash
sudo apt update && sudo apt upgrade -y
sudo reboot               # se kernel atualizou
```

Após reboot, os containers sobem sozinhos (graças a `restart: unless-stopped`).

---

## 12. Troubleshooting

### `Caddy não consegue emitir certificado`

- Verifique que DNS está propagado: `dig api.peditriagem.com.br +short`
- Verifique que portas 80 e 443 estão abertas: `sudo ufw status`
- Veja logs: `docker compose logs caddy`
- Limite Let's Encrypt: 5 falhas/hora — espere ou troque para staging environment

### `triagem-api fica unhealthy`

- `docker compose logs triagem-api` — provavelmente erro de conexão com DB ou config faltando
- Verifique `.env` está populado: `cat .env`
- Verifique que `postgres` está healthy: `docker compose ps`

### `Cannot connect to the Docker daemon`

- Você esqueceu de logar de novo após `usermod -aG docker deploy`. Saia e reentre via SSH.

### `Permission denied (publickey)` no SSH do CI

- A chave privada no `SERVER_SSH_KEY` precisa **incluir as linhas** `-----BEGIN OPENSSH PRIVATE KEY-----` e `-----END OPENSSH PRIVATE KEY-----`.
- A pública correspondente precisa estar no `~/.ssh/authorized_keys` do usuário `deploy` na VM.
- Teste local: `ssh -i /caminho/da/chave deploy@<IP>` antes de colocar no secret.

### `Disco cheio depois de muitos deploys`

```bash
docker system prune -af --volumes        # remove imagens/volumes não usados
sudo journalctl --vacuum-time=7d         # limpa logs systemd antigos
```

### `Postgres corrompido depois de reboot abrupto`

- Restaure do último backup (§10.4).
- Considere ativar fsync (já é padrão) e aumentar `checkpoint_timeout`.

---

## 13. Checklist final

Antes de considerar a infra "pronta para uso":

### Segurança

- [ ] Login root via SSH desabilitado
- [ ] Senha SSH desabilitada (só por chave)
- [ ] UFW ativo, só portas 22/80/443 abertas
- [ ] Fail2ban rodando
- [ ] Postgres NÃO exposto na internet
- [ ] `.env` com permissão 600
- [ ] Secrets do GitHub configurados
- [ ] Senhas geradas com `openssl rand`, não inventadas

### Funcional

- [ ] HTTPS funcionando (cadeado verde no browser)
- [ ] `https://.../health` retorna 200
- [ ] Swagger UI carrega
- [ ] Deploy via tag `v*` funciona ponta-a-ponta
- [ ] Aprovação manual em prod ativa

### Operacional

- [ ] Backup diário do Postgres rodando via cron
- [ ] Restore de backup foi testado pelo menos uma vez
- [ ] `docker stats` mostra uso saudável (<70% CPU/RAM em idle)
- [ ] Reboot da VM volta tudo sozinho
- [ ] Pelo menos 2 pessoas sabem fazer deploy manual

### Documentação

- [ ] Time sabe onde acessar logs
- [ ] Time sabe como fazer rollback
- [ ] Senha do `deploy` num gerenciador (Bitwarden, 1Password)
- [ ] Procedimento de "perdi a chave SSH" documentado

---

**Última atualização:** 2026-05-06
**Autor:** Time PJI Triagem Pediátrica
