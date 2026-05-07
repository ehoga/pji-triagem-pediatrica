# Setup Postgres + Spring Boot + Flyway (passo-a-passo do zero)

> Tutorial para implementar a task **F00-T01** ([`FEATURES.md`](./FEATURES.md)).
> Pensado para quem **nunca configurou Docker, Postgres, migrations ou variáveis de ambiente**.
> Cada passo explica o **que fazer** e o **porquê**.
> Para entender a estratégia geral de migrations, ver [`MIGRATIONS.md`](./MIGRATIONS.md).
> Para entender o deploy em produção (que é diferente de dev local), ver [`INFRAESTRUTURA.md`](./INFRAESTRUTURA.md).

**Tempo estimado:** 2h a 2h30 se for sua primeira vez. 30 min depois que entender.

---

## Sumário

1. [⚠ Antes de tudo: a confusão clássica sobre "secrets"](#1-antes-de-tudo-a-confusão-clássica-sobre-secrets)
2. [Conceitos que você precisa entender](#2-conceitos-que-você-precisa-entender)
3. [O fluxo completo (mapa mental)](#3-o-fluxo-completo-mapa-mental)
4. [Pré-requisitos: instalar Docker Desktop](#4-pré-requisitos-instalar-docker-desktop)
5. [Passo 1: criar `docker-compose.yml`](#5-passo-1-criar-docker-composeyml)
6. [Passo 2: criar `.env` (local) e `.env.example` (versionado)](#6-passo-2-criar-env-local-e-envexample-versionado)
7. [Passo 3: atualizar `.gitignore`](#7-passo-3-atualizar-gitignore)
8. [Passo 4: corrigir o `pom.xml` (Postgres + Flyway)](#8-passo-4-corrigir-o-pomxml)
9. [Passo 5: configurar `application.properties`](#9-passo-5-configurar-applicationproperties)
10. [Passo 6: criar as migrations Flyway](#10-passo-6-criar-as-migrations-flyway)
11. [Passo 7: subir o Postgres](#11-passo-7-subir-o-postgres)
12. [Passo 8: subir o Spring Boot e validar](#12-passo-8-subir-o-spring-boot-e-validar)
13. [Quando entra GitHub Secrets (e quando NÃO entra)](#13-quando-entra-github-secrets-e-quando-não-entra)
14. [Comandos do dia-a-dia](#14-comandos-do-dia-a-dia)
15. [Troubleshooting](#15-troubleshooting)
16. [Checklist final da F00-T01](#16-checklist-final-da-f00-t01)

---

## 1. ⚠ Antes de tudo: a confusão clássica sobre "secrets"

Você disse que quer usar **GitHub Secrets para salvar as variáveis**. Isso é uma confusão MUITO comum de iniciante. Vamos clarear antes de continuar:

| Onde você está | O que usa para variáveis |
|---|---|
| **Sua máquina, rodando localmente** (esse tutorial) | Arquivo `.env` (NÃO vai pro Git) |
| **GitHub Actions rodando no PR ou em deploy** | GitHub Secrets |
| **Servidor de produção** | Arquivo `.env` no servidor + chave SSH protegida |

**GitHub Secrets só é lido por workflows do `.github/workflows/*.yml`.** Quando você roda o backend na sua máquina (`./mvnw spring-boot:run`), o GitHub Secrets **não existe pra você** — sua máquina não conversa com o GitHub no momento de rodar o app.

Então o que vamos fazer agora é:

1. Configurar `.env` **localmente** para rodar na sua máquina ✅
2. Criar `.env.example` **versionado** para outros devs saberem quais variáveis configurar ✅
3. Documentar como **vão entrar no GitHub Secrets depois** quando criarem a pipeline (F00-T05) — mas **não agora**

Beleza? Continua.

---

## 2. Conceitos que você precisa entender

Leia isso aqui antes de tocar em qualquer arquivo. 7 minutos. Vai economizar 1 hora de confusão.

### 2.1 O que é Docker (em 3 frases)

Docker é um programa que **roda outros programas dentro de "caixinhas isoladas"** chamadas containers. Cada container tem seu próprio sistema operacional mini, suas próprias dependências, e não bagunça a sua máquina. Em vez de instalar o Postgres direto no Windows, a gente roda o **Postgres dentro de um container Docker** — é mais limpo, mais fácil de tirar se der errado, e **todo mundo do time roda exatamente a mesma versão**.

### 2.2 O que é Docker Compose

Docker sozinho roda **um container**. Docker Compose é um "controle remoto" que sobe **vários containers juntos** seguindo um arquivo de receita (`docker-compose.yml`). Por enquanto vamos subir só o Postgres, mas depois (em produção) o mesmo Compose vai subir Postgres + API + reverse proxy juntos.

### 2.3 O que é "volume nomeado"

Container Docker é descartável. Se você apagar o container, os dados dentro **somem**. Volume é um "pendrive virtual" que fica **fora do container** mas **plugado nele**. Mesmo que o container morra, os dados ficam guardados no volume.

A receita do Postgres com volume nomeado:

- Container vai e vem
- Volume `postgres_data` segura os arquivos `.dat` do banco
- Você pode `docker compose down` e `docker compose up` 50 vezes — dados não somem

### 2.4 O que é healthcheck

Container "subir" não significa "estar pronto pra trabalhar". Postgres sobe em ~5 segundos, mas demora mais 2-3s pra aceitar conexões. Healthcheck é um teste periódico que pergunta "tá pronto pra trabalhar?". O Spring Boot vai esperar o Postgres ficar **healthy** antes de tentar conectar.

### 2.5 O que é variável de ambiente

É um valor que vive **fora do código**, no sistema operacional. Exemplo: `DB_PASSWORD=triagem123`. O programa lê essa variável quando precisa, em vez de ter `triagem123` escrito no código-fonte. Por quê? Porque:

1. **Senha não pode ir pro Git.** Se você commitar uma senha, ela vira pública pra sempre (mesmo apagando depois — Git guarda histórico).
2. **Cada ambiente tem valores diferentes.** Dev usa `triagem123`, produção usa uma senha forte de verdade. O **código** é o mesmo nos dois — só a variável muda.

### 2.6 Como o Spring Boot lê configuração

Spring lê arquivos `application.properties` (ou `application.yml`). Dentro deles, você pode usar a sintaxe `${VARIAVEL:valor_default}` para puxar de uma variável de ambiente, com um fallback se ela não existir.

Exemplo:

```properties
spring.datasource.password=${DB_PASSWORD:triagem}
```

Significa: "leia a variável `DB_PASSWORD`. Se não existir, use `triagem` como padrão". Em dev local funciona com o default. Em produção a variável estará setada de verdade.

### 2.7 `.env` vs `.env.example`

- **`.env`**: arquivo real com os valores. Fica **só na sua máquina**. Nunca vai pro Git.
- **`.env.example`**: arquivo modelo com os nomes das variáveis e valores **fake**. Vai pro Git. Serve pra outro dev clonar o repo, copiar `.env.example` → `.env`, preencher com valores reais e rodar.

```
.env.example       (vai pro Git)         DB_PASSWORD=trocar_aqui
.env               (NÃO vai pro Git)     DB_PASSWORD=minhasenha123
```

### 2.8 O que é Flyway e por que usar desde o dia 1

**Migration** é um arquivo SQL que descreve uma mudança no schema do banco (criar tabela, adicionar coluna, etc.). **Flyway** é uma biblioteca que:

1. Lê os arquivos de migration (`V1__init.sql`, `V2__add_coluna.sql`, ...)
2. Aplica em ordem na primeira inicialização da app
3. **Anota numa tabela de controle** (`flyway_schema_history`) quais já rodaram
4. Próxima vez que a app sobe, só roda as **novas**

A alternativa seria deixar o Hibernate criar tabelas sozinho (`ddl-auto=update`), mas isso é **perigoso em produção** (já discutido em [`MIGRATIONS.md` §1](./MIGRATIONS.md#1-o-problema-por-que-migrations-explícitas)). Como vamos pra produção depois, **vale aprender certo agora** em vez de retrabalhar.

**Mudança de mindset importante:** com Flyway, o **schema do banco é a fonte de verdade**. Você define a tabela no SQL primeiro, depois cria a `@Entity` Java pra refletir o schema. O Hibernate fica em modo `validate` — só **confere** se entity bate com schema, sem mexer.

---

## 3. O fluxo completo (mapa mental)

```
                    ┌───────────────────────────────┐
                    │  1. Instalar Docker Desktop   │
                    └──────────────┬────────────────┘
                                   ▼
                    ┌───────────────────────────────┐
                    │  2. Criar docker-compose.yml  │
                    │     na raiz do repo           │
                    └──────────────┬────────────────┘
                                   ▼
                    ┌───────────────────────────────┐
                    │  3. Criar .env e .env.example │
                    │     dentro de backend/        │
                    └──────────────┬────────────────┘
                                   ▼
                    ┌───────────────────────────────┐
                    │  4. Atualizar .gitignore      │
                    │     pra não commitar .env     │
                    └──────────────┬────────────────┘
                                   ▼
                    ┌───────────────────────────────┐
                    │  5. Corrigir pom.xml          │
                    │     (Postgres + Flyway)       │
                    └──────────────┬────────────────┘
                                   ▼
                    ┌───────────────────────────────┐
                    │  6. Editar                    │
                    │     application.properties    │
                    └──────────────┬────────────────┘
                                   ▼
                    ┌───────────────────────────────┐
                    │  7. Criar V1__init.sql        │
                    │     (schema inicial)          │
                    └──────────────┬────────────────┘
                                   ▼
                    ┌───────────────────────────────┐
                    │  8. docker compose up -d      │
                    │     (sobe Postgres)           │
                    └──────────────┬────────────────┘
                                   ▼
                    ┌───────────────────────────────┐
                    │  9. ./mvnw spring-boot:run    │
                    │     (Flyway aplica V1, Spring │
                    │     conecta no banco)         │
                    └──────────────┬────────────────┘
                                   ▼
                    ┌───────────────────────────────┐
                    │ 10. Verificar:                │
                    │     - Flyway aplicou V1       │
                    │     - 7 tabelas + history     │
                    │     - Spring sem erros        │
                    └───────────────────────────────┘
```

---

## 4. Pré-requisitos: instalar Docker Desktop

### Windows

1. Vá em https://www.docker.com/products/docker-desktop/
2. Baixe **Docker Desktop for Windows**
3. Execute o instalador. Marque **WSL 2** se perguntar (é a opção moderna, mais leve)
4. Reinicie o computador
5. Abra o Docker Desktop. Espere o ícone da baleia ficar verde na bandeja do sistema
6. Abra o PowerShell e teste:

```powershell
docker --version
# deve aparecer: Docker version 27.x.x

docker compose version
# deve aparecer: Docker Compose version v2.x.x

docker run --rm hello-world
# deve baixar a imagem e imprimir uma mensagem amigável
```

Se os 3 comandos funcionaram, ✓ Docker está pronto.

**Nota Windows:** Docker Desktop precisa do **WSL 2** habilitado. Se der erro, abra PowerShell como administrador e rode:

```powershell
wsl --install
# reinicie depois
```

### Linux

```bash
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
# saia e entre de novo no terminal
docker --version
```

### Mac

Baixe Docker Desktop pra Mac em docker.com (mesmo site). Instalador `.dmg`.

---

## 5. Passo 1: criar `docker-compose.yml`

### O que fazer

Crie o arquivo **`docker-compose.yml`** na **raiz do repositório** (mesmo nível de `README.md`):

```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: triagem-postgres
    restart: unless-stopped
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: ${POSTGRES_DB:-triagem}
      POSTGRES_USER: ${POSTGRES_USER:-triagem}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-triagem}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-triagem} -d ${POSTGRES_DB:-triagem}"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
```

### O porquê de cada linha

| Linha | Por quê |
|---|---|
| `services:` | Lista de containers que vamos subir. Por enquanto só `postgres`. |
| `image: postgres:16-alpine` | Imagem oficial do Postgres versão 16, "alpine" = versão mini do Linux (mais leve). |
| `container_name: triagem-postgres` | Nome amigável pro container (em vez do nome aleatório que Docker daria). |
| `restart: unless-stopped` | Se o container cair (ou você reiniciar a máquina), Docker sobe ele de novo automaticamente. |
| `ports: - "5432:5432"` | Mapeia porta 5432 do container pra porta 5432 da sua máquina. Spring Boot na sua máquina vai conectar em `localhost:5432`. |
| `environment:` | Variáveis que o container Postgres lê na primeira inicialização pra criar o banco/usuário. |
| `${POSTGRES_DB:-triagem}` | Sintaxe do Compose: leia variável `POSTGRES_DB`; se não existir, use `triagem`. |
| `volumes: postgres_data:/var/lib/postgresql/data` | Plugue o volume `postgres_data` no caminho onde Postgres guarda dados. Volume sobrevive a `docker compose down`. |
| `healthcheck:` | Define como Docker testa "tá pronto?". `pg_isready` é um comando do próprio Postgres. |
| `volumes: postgres_data:` (no final) | Declara o volume nomeado. Docker cria automaticamente se não existir. |

### Coisa importante de notar

A senha aqui usa **`${POSTGRES_PASSWORD:-triagem}`** com fallback `triagem`. Isso significa: **se você não criar nenhum `.env`**, o Postgres vai subir com user/pass `triagem/triagem`. Em prod a gente vai SETAR a variável, então o fallback não é usado.

---

## 6. Passo 2: criar `.env` (local) e `.env.example` (versionado)

### O que fazer

**Crie dois arquivos** dentro de `backend/`:

**`backend/.env`** (esse vai ficar SÓ na sua máquina):

```env
# ============================================
# .env — Variáveis locais de desenvolvimento
# NÃO commitar esse arquivo!
# ============================================

# Banco de dados
DB_HOST=localhost
DB_PORT=5432
DB_NAME=triagem
DB_USER=triagem
DB_PASSWORD=triagem

# JWT (será usado quando F01 for implementada)
APP_JWT_SECRET=dev-secret-trocar-em-prod-com-chave-forte-de-no-minimo-64-caracteres-aleatorios
APP_JWT_EXPIRATION_MS=86400000
```

**`backend/.env.example`** (esse vai pro Git, serve de modelo):

```env
# ============================================
# .env.example — Modelo de variáveis de ambiente
# Copie esse arquivo para .env e preencha os valores reais.
# Em prod, valores virão do servidor (não desse arquivo).
# ============================================

# Banco de dados (apontando para o postgres do docker-compose)
DB_HOST=localhost
DB_PORT=5432
DB_NAME=triagem
DB_USER=triagem
DB_PASSWORD=triagem

# JWT — gere uma chave forte para PROD com:
#   openssl rand -base64 64
# Em dev, pode usar valor placeholder (mas com tamanho mínimo de 64 chars)
APP_JWT_SECRET=dev-secret-trocar-em-prod-com-chave-forte-de-no-minimo-64-caracteres-aleatorios
APP_JWT_EXPIRATION_MS=86400000
```

### O porquê

- **`.env` só na sua máquina:** porque tem (no futuro) senha real, chave JWT real. Se commitar, vira público.
- **`.env.example` no Git:** porque outro dev vai clonar o repo e precisa saber **quais variáveis configurar**. O `.example` lista os nomes com valores fake/placeholder.
- **Mesmas chaves em ambos os arquivos:** mantém os dois sincronizados. Se você adicionar variável nova em `.env`, **adicione também em `.env.example`** (sem o valor real).

### Por que dentro de `backend/` e não na raiz?

Convenção: o `.env` é da aplicação que usa ele. Quem lê essas variáveis é o backend (Spring Boot). Frontend tem suas próprias variáveis (em `frontend/`). O `docker-compose.yml` na raiz **também lê** variáveis quando você roda — Docker Compose por padrão lê o `.env` no mesmo diretório do `docker-compose.yml`.

**Solução:** vamos colocar **dois `.env`**:

- `backend/.env` — configs do Spring Boot (DB_HOST, JWT, etc.)
- (raiz)/`.env` — configs do docker-compose.yml (POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD)

Para começar simples, crie também um `.env` na **raiz do repo** com as 3 variáveis do Postgres:

**`.env`** (na raiz, junto do `docker-compose.yml`):

```env
POSTGRES_DB=triagem
POSTGRES_USER=triagem
POSTGRES_PASSWORD=triagem
```

E o **`.env.example`** correspondente na raiz:

```env
POSTGRES_DB=triagem
POSTGRES_USER=triagem
POSTGRES_PASSWORD=triagem
```

(em dev pode ter o mesmo valor — em prod vão divergir)

> **Resumo de arquivos:**
> ```
> pji-triagem-pediatrica/
> ├── docker-compose.yml         ← versionado
> ├── .env                       ← NÃO versionado (raiz, vars do Compose)
> ├── .env.example               ← versionado (raiz)
> └── backend/
>     ├── .env                   ← NÃO versionado (vars do Spring)
>     └── .env.example           ← versionado
> ```

---

## 7. Passo 3: atualizar `.gitignore`

### O que fazer

Abra o `.gitignore` na **raiz do repositório** e adicione no final:

```gitignore
# ============================================
# Variáveis de ambiente — NUNCA commitar
# ============================================
.env
*.env
!.env.example

# Docker overrides locais
docker-compose.override.yml

# Logs e dados de volume locais (caso Docker bind-mount em dev)
postgres_data/
*.log
```

### O porquê linha por linha

- **`.env`** — bloqueia o `.env` da raiz
- **`*.env`** — bloqueia qualquer arquivo `*.env` (ex: `prod.env`, `staging.env`)
- **`!.env.example`** — exceção: PERMITE que o `.env.example` seja commitado (o `!` desfaz a regra anterior pra esse arquivo)
- **`docker-compose.override.yml`** — arquivo opcional do Compose que serve pra overrides locais. Não deve ir pro Git.
- **`postgres_data/`** — caso alguém configure bind mount em vez de volume nomeado, evita commitar dados do banco
- **`*.log`** — arquivos de log

### Verificar

Depois de salvar, rode:

```powershell
git status
```

O arquivo `.env` **NÃO** deve aparecer como "untracked". Se aparecer, ele está sendo trackeado — confira o `.gitignore` de novo. Se já estava trackeado por engano:

```powershell
git rm --cached .env backend/.env
```

E depois faça commit. O arquivo continua na sua máquina, mas o Git para de rastrear.

---

## 8. Passo 4: corrigir o `pom.xml`

O `pom.xml` atual tem vários problemas que mencionei na [`ARQUITETURA.md` §13](./ARQUITETURA.md#13-observações-sobre-o-pomxml-atual). Vamos arrumar e já adicionar Flyway.

### O que fazer

Substitua o **`backend/pom.xml`** por este (lê com calma o "porquê" depois):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.2</version>
        <relativePath/>
    </parent>

    <groupId>com.pji</groupId>
    <artifactId>triagem</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>triagem</name>
    <description>Sistema de Triagem Pediatrica - Backend</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <!-- Web (controllers REST) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- JPA (persistência) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- Bean Validation (@NotNull, @Email, etc.) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Driver do PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Flyway (gerenciamento de schema/migrations) -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>

        <!-- DevTools (hot reload em dev) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Lombok (menos boilerplate) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- spring-dotenv (lê .env automaticamente) -->
        <dependency>
            <groupId>me.paulschwarz</groupId>
            <artifactId>spring-dotenv</artifactId>
            <version>4.0.0</version>
        </dependency>

        <!-- Testes -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### O porquê das mudanças

| O que mudou | Por quê |
|---|---|
| Spring Boot `4.0.6` → `3.4.2` | A versão 4.x é beta/cutting-edge. 3.4.2 é estável e é o que o `PLAN.md` planeja. |
| Removido `spring-boot-h2console` | Esse artifact **nem existe**. E o plano diz pra remover H2. |
| Removido `h2` runtime | Mesma coisa — vamos usar Postgres. |
| `spring-boot-starter-webmvc` → `spring-boot-starter-web` | Nome correto do starter. O outro não existe. |
| Removidos `*-test` separados | Só `spring-boot-starter-test` (singular) — ele já cobre JUnit, Mockito, AssertJ, MockMvc, JsonPath, etc. |
| Adicionado `org.postgresql:postgresql` | Driver JDBC pro Spring conversar com Postgres. |
| Adicionado `flyway-core` + `flyway-database-postgresql` | Flyway para migrations + módulo específico do Postgres (necessário a partir de Flyway 10). |
| Adicionado `spring-dotenv` | Faz Spring Boot ler o `.env` automaticamente. |
| `runtime` scope no Postgres | Driver JDBC só é usado em runtime, não em compile time. |

> **Sobre Testcontainers:** o plano (F08) menciona Testcontainers para testes de integração. Eu **deixaria de adicionar agora** — vocês adicionam quando começarem F08. Princípio: só pegue dependência quando for usar.

---

## 9. Passo 5: configurar `application.properties`

### O que fazer

Substitua **`backend/src/main/resources/application.properties`** por:

```properties
# ============================================
# application.properties
# Config padrão do Spring Boot (dev local)
# ============================================

spring.application.name=triagem

# --------------------------------------------
# Servidor HTTP
# --------------------------------------------
server.port=8080

# --------------------------------------------
# Banco de dados (PostgreSQL)
# Lê variáveis de ambiente; usa defaults se não setadas
# --------------------------------------------
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:triagem}
spring.datasource.username=${DB_USER:triagem}
spring.datasource.password=${DB_PASSWORD:triagem}
spring.datasource.driver-class-name=org.postgresql.Driver

# --------------------------------------------
# JPA / Hibernate
# --------------------------------------------
# validate: Hibernate APENAS confere que entities batem com schema.
# NÃO cria nem altera nada — quem manda no schema é o Flyway.
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# --------------------------------------------
# Flyway (controla o schema do banco)
# --------------------------------------------
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true

# --------------------------------------------
# JWT (será usado quando F01 for implementada)
# --------------------------------------------
app.jwt.secret=${APP_JWT_SECRET:dev-secret-trocar-em-prod-com-chave-forte-de-no-minimo-64-caracteres-aleatorios}
app.jwt.expiration-ms=${APP_JWT_EXPIRATION_MS:86400000}

# --------------------------------------------
# Logs
# --------------------------------------------
logging.level.org.springframework=INFO
logging.level.org.flywaydb=INFO
logging.level.com.pji.triagem=DEBUG
```

### O porquê

**`spring.datasource.url`** usa a sintaxe `${DB_HOST:localhost}`:

- Se a variável `DB_HOST` existe no ambiente, usa ela
- Se não existe, usa `localhost`

Resultado: você consegue rodar mesmo **sem** ter um `.env` configurado. Os defaults batem com o setup do Docker Compose.

**`spring.jpa.hibernate.ddl-auto=validate`** é a mudança mais importante:

| Modo | O que faz | Quando usar |
|---|---|---|
| `none` | Hibernate não toca no schema | Combina com Flyway (alternativa válida) |
| `validate` | Confere que `@Entity` casa com tabela. Falha no startup se divergir. | **Recomendado com Flyway** ✓ |
| `update` | Cria/altera tabelas baseado em `@Entity` | OK em dev rápido. **NUNCA em prod.** |
| `create` | Dropa tudo e recria | Só em testes |
| `create-drop` | Dropa tudo no shutdown | Só em testes |

Com Flyway, **o schema é fonte de verdade**. Hibernate não cria nem altera nada — só **confere** que sua `@Entity` Java bate com a tabela criada pela migration.

> ⚠ Se você definir uma `@Entity` que não tem tabela correspondente, **Spring vai falhar ao subir** com `Schema-validation: missing table`. Isso é proposital — força você a criar a migration primeiro.

**`spring.flyway.*`**: configuração do Flyway:

- `enabled=true` — liga o Flyway no startup
- `locations=classpath:db/migration` — onde procurar os arquivos `V*__*.sql`
- `baseline-on-migrate=true` — útil se um dia plugar Flyway num banco que já tem tabelas. No nosso caso é seguro ligar.
- `validate-on-migrate=true` — Flyway compara checksums das migrations. Se alguém **alterar** um `V*__*.sql` que já rodou, app falha ao subir. Proteção sagrada.

**`spring.jpa.show-sql=true`**: imprime no console todo SQL que o Hibernate gerou. Útil pra debugar. Em prod desliga.

**`logging.level.org.flywaydb=INFO`**: mostra logs do Flyway no startup (ex: "Successfully applied 1 migration"). Importante pra você confirmar que rodou.

---

## 10. Passo 6: criar as migrations Flyway

### Por que migrations agora (e não depois)?

Versão "fácil" deste tutorial usaria `ddl-auto=update` e Hibernate criaria tabelas sozinho conforme você criasse `@Entity`. Funciona em dev, mas tem 2 problemas:

1. **Em produção é proibido.** ddl-auto pode dropar coluna sem aviso ou criar diferenças entre ambientes. Vocês vão ter que migrar pra Flyway antes do primeiro deploy de qualquer jeito.
2. **Mindset diferente.** Com Flyway, **schema é fonte de verdade** — você define a tabela primeiro, depois o Java reflete ela. Esse mindset é o profissional. Vale internalizar agora.

> Para entender a estratégia completa, ler [`MIGRATIONS.md`](./MIGRATIONS.md). Essa seção é o **mínimo necessário** pra rodar.

### 10.1 Criar a estrutura de pastas

Dentro de `backend/src/main/resources/`, crie a pasta `db/migration/`:

```
backend/src/main/resources/
├── application.properties
└── db/
    └── migration/
        └── V1__init.sql       ← você vai criar isso
```

Pelo PowerShell:

```powershell
cd backend\src\main\resources
mkdir db\migration
```

### 10.2 Criar o `V1__init.sql`

Crie o arquivo **`backend/src/main/resources/db/migration/V1__init.sql`** com este conteúdo:

```sql
-- =========================================================================
-- V1__init.sql
-- Schema inicial do PediTriagem
-- =========================================================================

-- -------------------------------------------------------------------------
-- Tipos enumerados
-- -------------------------------------------------------------------------
CREATE TYPE tipo_pergunta AS ENUM ('OPTIONS', 'YESNO');
CREATE TYPE classificacao AS ENUM ('LOW', 'MOD', 'HIGH');

-- -------------------------------------------------------------------------
-- Usuário
-- -------------------------------------------------------------------------
CREATE TABLE usuario (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    senha       VARCHAR(255) NOT NULL,         -- bcrypt hash
    criado_em   TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_usuario_nome_min CHECK (LENGTH(TRIM(nome)) >= 2),
    CONSTRAINT chk_usuario_email_format CHECK (email ~* '^[^@\s]+@[^@\s]+\.[^@\s]+$')
);

CREATE INDEX idx_usuario_email ON usuario(email);

-- -------------------------------------------------------------------------
-- Criança
-- -------------------------------------------------------------------------
CREATE TABLE crianca (
    id                BIGSERIAL    PRIMARY KEY,
    usuario_id        BIGINT       NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    nome              VARCHAR(100) NOT NULL,
    data_nascimento   DATE         NOT NULL,
    peso_kg           NUMERIC(5,2),
    avatar_emoji      VARCHAR(8)   NOT NULL DEFAULT '🌸',
    criado_em         TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_crianca_nome_min CHECK (LENGTH(TRIM(nome)) >= 1),
    CONSTRAINT chk_crianca_data_passada CHECK (data_nascimento <= CURRENT_DATE),
    CONSTRAINT chk_crianca_peso_positivo CHECK (peso_kg IS NULL OR peso_kg > 0)
);

CREATE INDEX idx_crianca_usuario ON crianca(usuario_id);

-- -------------------------------------------------------------------------
-- Sintoma (catálogo)
-- -------------------------------------------------------------------------
CREATE TABLE sintoma (
    id                BIGSERIAL    PRIMARY KEY,
    codigo            VARCHAR(40)  NOT NULL UNIQUE,
    nome              VARCHAR(100) NOT NULL,
    descricao_curta   VARCHAR(255),
    icone_ref         VARCHAR(40),
    cor_hex           VARCHAR(7),
    ordem             INT          NOT NULL DEFAULT 0,

    CONSTRAINT chk_sintoma_cor_hex CHECK (cor_hex IS NULL OR cor_hex ~* '^#[0-9a-f]{6}$')
);

-- -------------------------------------------------------------------------
-- Pergunta (de um sintoma)
-- -------------------------------------------------------------------------
CREATE TABLE pergunta (
    id           BIGSERIAL     PRIMARY KEY,
    sintoma_id   BIGINT        NOT NULL REFERENCES sintoma(id) ON DELETE CASCADE,
    codigo       VARCHAR(40)   NOT NULL,
    texto        VARCHAR(500)  NOT NULL,
    sub          VARCHAR(255),
    tipo         tipo_pergunta NOT NULL,
    ordem        INT           NOT NULL DEFAULT 0,

    CONSTRAINT uq_pergunta_codigo_sintoma UNIQUE (sintoma_id, codigo)
);

CREATE INDEX idx_pergunta_sintoma ON pergunta(sintoma_id);

-- -------------------------------------------------------------------------
-- Opção de pergunta (para tipo OPTIONS)
-- -------------------------------------------------------------------------
CREATE TABLE opcao_pergunta (
    id            BIGSERIAL    PRIMARY KEY,
    pergunta_id   BIGINT       NOT NULL REFERENCES pergunta(id) ON DELETE CASCADE,
    codigo        VARCHAR(40)  NOT NULL,
    texto         VARCHAR(500) NOT NULL,
    score         INT          NOT NULL,
    red_flag      BOOLEAN      NOT NULL DEFAULT FALSE,
    ordem         INT          NOT NULL DEFAULT 0,

    CONSTRAINT uq_opcao_codigo_pergunta UNIQUE (pergunta_id, codigo),
    CONSTRAINT chk_opcao_score_range CHECK (score BETWEEN 0 AND 10)
);

CREATE INDEX idx_opcao_pergunta ON opcao_pergunta(pergunta_id);

-- -------------------------------------------------------------------------
-- Peso para pergunta YESNO
-- -------------------------------------------------------------------------
CREATE TABLE peso_yesno (
    id                  BIGSERIAL PRIMARY KEY,
    pergunta_id         BIGINT    NOT NULL UNIQUE REFERENCES pergunta(id) ON DELETE CASCADE,
    score_yes           INT       NOT NULL,
    score_no            INT       NOT NULL,
    score_dunno         INT       NOT NULL,
    red_flag_on_yes     BOOLEAN   NOT NULL DEFAULT FALSE,
    red_flag_on_no      BOOLEAN   NOT NULL DEFAULT FALSE,

    CONSTRAINT chk_yesno_score_yes_range   CHECK (score_yes   BETWEEN 0 AND 10),
    CONSTRAINT chk_yesno_score_no_range    CHECK (score_no    BETWEEN 0 AND 10),
    CONSTRAINT chk_yesno_score_dunno_range CHECK (score_dunno BETWEEN 0 AND 10)
);

-- -------------------------------------------------------------------------
-- Avaliação (cada triagem realizada)
-- -------------------------------------------------------------------------
CREATE TABLE avaliacao (
    id                   BIGSERIAL     PRIMARY KEY,
    crianca_id           BIGINT        NOT NULL REFERENCES crianca(id) ON DELETE CASCADE,
    sintoma_id           BIGINT        NOT NULL REFERENCES sintoma(id),
    classificacao        classificacao NOT NULL,
    score                INT           NOT NULL,
    red_flag_detected    BOOLEAN       NOT NULL DEFAULT FALSE,
    respostas            JSONB         NOT NULL,
    protocolo_versao     VARCHAR(20)   NOT NULL DEFAULT '1.0.0',
    criado_em            TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_avaliacao_score_min CHECK (score >= 0)
);

-- Index para a query mais comum: histórico por criança
CREATE INDEX idx_avaliacao_crianca_data ON avaliacao(crianca_id, criado_em DESC);

-- Index para filtro por classificação (stats)
CREATE INDEX idx_avaliacao_classificacao ON avaliacao(classificacao);
```

### 10.3 O porquê desse SQL

Esse é o **schema completo do MVP**, baseado no modelo de dados em [`ARQUITETURA.md` §6](./ARQUITETURA.md#6-modelo-de-dados-er-e-por-que-ele-é-assim). Antes de assustar com tamanho — você só vai escrever uma vez. Pontos-chave:

| Decisão | Por quê |
|---|---|
| **Tipos ENUM nativos** (`tipo_pergunta`, `classificacao`) | Postgres entende ENUM. Mais seguro que VARCHAR(20) — banco rejeita valor inválido. |
| **CHECK constraints** | Validação no nível do banco. Mesmo que alguém faça INSERT direto sem passar pelo Java, o banco rejeita. **Princípio: defesa em profundidade.** |
| **ON DELETE CASCADE** | Deletar usuário → crianças e avaliações vão junto. (Trade-off discutido em ARQUITETURA.md — para MVP é OK.) |
| **JSONB em respostas** | Postgres entende JSON nativamente, com índices e queries específicas. Mais flexível que tabela separada. |
| **idx_avaliacao_crianca_data** | A query mais comum vai ser "histórico de uma criança ordenado por data". Index composto otimiza. |
| **protocolo_versao** | Auditoria — saber qual versão do protocolo classificou cada avaliação. Sugestão da §11.3 da ARQUITETURA já implementada. |
| **BIGSERIAL** | PK auto-incremento de 64 bits (BIGINT). Não vai estourar. |
| **TIMESTAMP DEFAULT NOW()** | Auditoria automática. |

### 10.4 Verificar a convenção de nome do arquivo

Confira que o nome do arquivo é exatamente:

```
V1__init.sql
```

- `V` maiúsculo
- `1` (não `V01`, não `V1.0`)
- `__` (DOIS underscores)
- `init` ou qualquer descrição em snake_case
- extensão `.sql` minúscula

Flyway é estrito. Errar o nome = não roda.

### 10.5 E os seeds (dados iniciais como os 9 sintomas)?

**Não vão na V1.** Schema e dados são preocupações diferentes. Quando vocês forem implementar F03 (Catálogo de Sintomas), criem `V2__seed_sintomas.sql` com `INSERT INTO sintoma VALUES ...`. Cada feature que precisa de seed cria sua própria migration `V<n>__seed_<coisa>.sql`.

Por enquanto, V1 sozinho basta.

### 10.6 Quer adicionar uma migration depois?

Workflow rápido (detalhes em [`MIGRATIONS.md` §6](./MIGRATIONS.md#6-workflow-do-dev-adicionar-uma-migration)):

1. Veja qual o maior `V<n>` atual em `db/migration/`
2. Crie `V<n+1>__descricao.sql` com o SQL da mudança
3. Reinicie o Spring — Flyway aplica automaticamente
4. Atualize as `@Entity` Java pra refletir a mudança
5. PR

**Regra sagrada:** uma vez que uma migration foi aplicada (em qualquer ambiente), **NÃO altere o conteúdo dela**. Crie uma `V<n+1>` que conserta. Flyway vai bloquear se você tentar alterar.

---

## 11. Passo 7: subir o Postgres

Estamos quase lá!

### O que fazer

Abra o terminal na **raiz do repo** (onde está o `docker-compose.yml`) e:

```powershell
docker compose up -d
```

O `-d` significa "detached" (rodando em segundo plano). Sem ele, você fica preso no log.

### O que esperar

```
[+] Running 3/3
 ✔ Network pji-triagem-pediatrica_default       Created
 ✔ Volume "pji-triagem-pediatrica_postgres_data" Created
 ✔ Container triagem-postgres                    Started
```

### Verificar

```powershell
docker compose ps
```

Deve mostrar:

```
NAME                IMAGE                 STATUS              PORTS
triagem-postgres    postgres:16-alpine    Up 30 seconds       0.0.0.0:5432->5432/tcp
                                          (healthy)
```

A palavra mágica é **`(healthy)`**. Significa que o healthcheck passou e o Postgres está pronto.

Se aparecer `(health: starting)`, espera 10-20s e roda o comando de novo. Se ficar `(unhealthy)`, ver §15 (Troubleshooting).

### Conferir o banco por dentro (opcional)

```powershell
docker compose exec postgres psql -U triagem -d triagem
```

Vai abrir o cliente SQL do Postgres. Tente:

```sql
\dt           -- listar tabelas (não vai ter nenhuma — Flyway ainda não rodou)
\q            -- sair
```

Tabelas só vão aparecer depois que o Spring subir, porque é o **Spring que dispara o Flyway** no startup.

---

## 12. Passo 8: subir o Spring Boot e validar

### O que fazer

Em **outro terminal** (deixa o Postgres rodando no anterior), entre na pasta `backend/`:

```powershell
cd backend
./mvnw spring-boot:run
```

Primeira execução demora — Maven baixa todas as dependências. Vai levar uns 2-5 min. Da segunda em diante, é rápido.

### O que esperar

No log, procure as 4 linhas mágicas:

```
... INFO --- o.f.c.i.database.base.BaseDatabaseType : Database: jdbc:postgresql://localhost:5432/triagem (PostgreSQL 16.x)
... INFO --- o.f.core.internal.command.DbMigrate    : Current version of schema "public": << Empty Schema >>
... INFO --- o.f.core.internal.command.DbMigrate    : Migrating schema "public" to version "1 - init"
... INFO --- o.f.core.internal.command.DbMigrate    : Successfully applied 1 migration to schema "public", now at version v1 (execution time 00:00.234s)
```

Depois disso, o Spring continua e termina com:

```
... INFO --- HikariPool-1 - Start completed.
... INFO --- TomcatWebServer  : Tomcat started on port 8080
... INFO --- TriagemApplication : Started TriagemApplication in 8.234 seconds
```

Os 5 sinais de sucesso:

- ✓ **Flyway encontrou o banco**
- ✓ **Successfully applied 1 migration** ← o V1 rodou!
- ✓ **HikariPool started** (conectou no Postgres)
- ✓ **Tomcat started on port 8080** (servidor HTTP subiu)
- ✓ **Started TriagemApplication** (aplicação inteira subiu sem erro)

### Confirmar que as tabelas foram criadas

Em outro terminal:

```powershell
docker compose exec postgres psql -U triagem -d triagem -c "\dt"
```

Você deve ver as **8 tabelas** (7 do schema + 1 de controle do Flyway):

```
                List of relations
 Schema |          Name           | Type  |  Owner
--------+-------------------------+-------+---------
 public | avaliacao               | table | triagem
 public | crianca                 | table | triagem
 public | flyway_schema_history   | table | triagem
 public | opcao_pergunta          | table | triagem
 public | pergunta                | table | triagem
 public | peso_yesno              | table | triagem
 public | sintoma                 | table | triagem
 public | usuario                 | table | triagem
(8 rows)
```

### Confirmar que Flyway registrou a aplicação da migration

```powershell
docker compose exec postgres psql -U triagem -d triagem -c "SELECT version, description, success FROM flyway_schema_history;"
```

Saída esperada:

```
 version | description | success
---------+-------------+---------
 1       | init        | t
(1 row)
```

`success=t` (true) = a migration rodou inteira sem erro. Se aparecer `f`, **algo deu errado** — ver §15 (Troubleshooting).

### Validar que `validate` está protegendo o schema

Pra ver `validate` em ação, faça um teste rápido:

1. Pare o Spring (Ctrl+C)
2. Rode no Postgres:
   ```sql
   docker compose exec postgres psql -U triagem -d triagem -c "ALTER TABLE usuario DROP COLUMN nome;"
   ```
3. Suba o Spring de novo: `./mvnw spring-boot:run`

Como ainda não temos `@Entity Usuario.java`, **vai subir mesmo sem a coluna** (validate só valida entidades existentes). Mas quando F01 criar a `Usuario.java` com `@Column nome`, o Spring vai falhar com `Schema-validation: missing column [nome] in table [usuario]`. Esse é o `validate` te protegendo.

Volta a coluna pro lugar (ou apague o volume e suba de novo):

```powershell
docker compose down -v
docker compose up -d
# espera ficar healthy, sobe o Spring de novo
```

🎉 Tudo pronto!

---

## 13. Quando entra GitHub Secrets (e quando NÃO entra)

Voltando à confusão original. Agora que você entendeu o fluxo local, a hora certa de mexer com GitHub Secrets:

### NÃO usa GitHub Secrets para:

- ❌ Rodar localmente (você está em dev local — usa `.env`)
- ❌ Configurar a máquina de outro dev (eles usam `.env.example` → `.env`)
- ❌ Configurar Docker Compose local

### USA GitHub Secrets para:

- ✅ Workflow de CI rodando testes (precisa de credenciais para conectar em DB de teste, se for o caso)
- ✅ Workflow de deploy fazendo SSH no servidor (precisa da chave SSH e IP do servidor)
- ✅ Workflow de build pegando token de registry (`GITHUB_TOKEN`, `DOCKER_HUB_TOKEN`, etc.)
- ✅ Qualquer valor que o **GitHub Actions** precise saber

### Exemplo concreto: configurando GitHub Secrets agora (preview)

Quando vocês forem fazer F00-T05 (CI do backend) e F11 (deploy), vão entrar em **Settings → Secrets and variables → Actions** do repo e adicionar:

| Nome | Valor | Quando usa |
|---|---|---|
| `SERVER_HOST` | IP do servidor de prod | Deploy |
| `SERVER_USER` | `deploy` | Deploy |
| `SERVER_SSH_KEY` | chave SSH privada | Deploy |
| `DB_PASSWORD_PROD` | senha forte do Postgres em prod | Deploy |
| `JWT_SECRET_PROD` | chave JWT forte de prod | Deploy |

E nos workflows YAML, vocês acessam com `${{ secrets.SERVER_HOST }}`:

```yaml
- uses: appleboy/ssh-action@v1
  with:
    host: ${{ secrets.SERVER_HOST }}      # vem do GitHub Secrets
    username: ${{ secrets.SERVER_USER }}
    key: ${{ secrets.SERVER_SSH_KEY }}
```

> **Mas isso é coisa para depois.** Agora foco em fazer rodar localmente.

### Detalhe importante de segurança

GitHub Secrets é **write-only** depois de criado — você seta o valor uma vez, depois só consegue **substituir**, nunca **ler de novo**. Isso é proposital: se um atacante invadir sua conta, ele não consegue extrair os secrets pra usar em outros lugares.

Para conferir se setou certo, o jeito é rodar um workflow que **usa** o secret e ver se funciona. Não dá pra "abrir e olhar".

---

## 14. Comandos do dia-a-dia

Salva esses na sua memória — você vai usar muito:

### Postgres / Docker Compose

```powershell
# subir tudo (Postgres)
docker compose up -d

# ver status
docker compose ps

# ver logs
docker compose logs -f postgres

# parar tudo (mantém dados)
docker compose stop

# parar e remover containers (mantém dados — volume nomeado é separado)
docker compose down

# ⚠ APAGAR TUDO (containers + volumes — perde dados, Flyway vai rodar tudo de novo)
docker compose down -v

# reiniciar só um serviço
docker compose restart postgres

# entrar no Postgres (cliente SQL)
docker compose exec postgres psql -U triagem -d triagem

# ver consumo de recursos
docker stats
```

### Spring Boot

```powershell
# rodar (a partir de backend/)
./mvnw spring-boot:run

# parar: Ctrl+C no terminal

# limpar build
./mvnw clean

# rodar testes
./mvnw test

# package (gera o .jar em target/)
./mvnw package
```

### Flyway útil (via psql)

```powershell
# ver histórico de migrations aplicadas
docker compose exec postgres psql -U triagem -d triagem -c "SELECT * FROM flyway_schema_history;"

# ver tabelas existentes
docker compose exec postgres psql -U triagem -d triagem -c "\dt"

# ver definição de uma tabela
docker compose exec postgres psql -U triagem -d triagem -c "\d usuario"
```

### Git (não esquece!)

```powershell
# antes de qualquer commit, confere que .env não está sendo trackeado
git status
# .env e backend/.env NÃO devem aparecer
```

---

## 15. Troubleshooting

### `Cannot connect to the Docker daemon`

Docker Desktop não está rodando. Abre o aplicativo Docker Desktop, espera o ícone da baleia ficar verde, tenta de novo.

### `Port 5432 is already in use`

Outro Postgres está rodando na sua máquina (talvez instalado nativamente). Duas opções:

1. **Parar o outro Postgres:**
   - Windows: `services.msc` → procura "postgresql" → Parar
   - Mac/Linux: `sudo systemctl stop postgresql` ou `brew services stop postgresql`

2. **Mudar a porta no docker-compose.yml:**
   ```yaml
   ports:
     - "5433:5432"   # 5433 na sua máquina, 5432 dentro do container
   ```
   E ajustar o `application.properties`: `${DB_PORT:5433}`.

### Spring Boot dá `Connection refused`

Postgres não está rodando ou ainda subindo. Confere:

```powershell
docker compose ps
```

Se não estiver `(healthy)`, espera 10s e tenta de novo. Se ficar `(unhealthy)` por muito tempo, vê os logs:

```powershell
docker compose logs postgres
```

### Spring Boot dá `password authentication failed`

A senha que Spring está tentando usar não bate com a do Postgres. Conferir:

- `docker-compose.yml` está com `POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-triagem}` (default `triagem`)
- `application.properties` está com `spring.datasource.password=${DB_PASSWORD:triagem}` (default `triagem`)

Os defaults batem (ambos `triagem`). Se mudou em algum lugar, confira que mudou nos dois.

Se mudou e quer voltar ao zero, **APAGUE O VOLUME**:

```powershell
docker compose down -v
docker compose up -d
```

Postgres recria do zero com a senha atual.

### `./mvnw: command not found` ou `permission denied`

Windows PowerShell:
```powershell
.\mvnw.cmd spring-boot:run
```

Linux/Mac (primeira vez precisa dar permissão):
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

### Flyway: `Validate failed: Migration checksum mismatch`

Você (ou alguém) **alterou um arquivo `V*__*.sql` que já tinha rodado**. Flyway detecta e bloqueia.

**Em dev local:** apague o volume e recomece (`docker compose down -v`). Flyway vai re-aplicar tudo do zero.

**Em prod:** NUNCA faça isso. Crie uma `V<n+1>__fix.sql` em vez. Detalhes em [`MIGRATIONS.md` §8](./MIGRATIONS.md#8-migrations-destrutivas-a-regra-forward-only).

### Flyway: `Detected resolved migration not applied to database: 1`

Flyway encontrou o `V1__init.sql` no classpath, mas o banco já tem alguma estrutura sem o registro do Flyway. Em dev fácil:

```powershell
docker compose down -v
docker compose up -d
```

Em prod, precisaria de baseline manual — fora do escopo desse tutorial.

### Flyway: `ERROR: type "tipo_pergunta" already exists`

Aconteceu um startup parcial antes — Postgres criou o tipo mas Flyway não terminou a transação. Limpa:

```powershell
docker compose down -v
docker compose up -d
```

### Spring: `Schema-validation: missing table [xyz]`

Você criou uma `@Entity` Java mas não tem migration que cria a tabela correspondente. Crie a migration `V<n>__add_xyz.sql` antes.

### Spring: `Schema-validation: missing column [xyz] in table [usuario]`

`@Entity Usuario` tem `@Column xyz` mas a tabela `usuario` não tem essa coluna. Crie migration `V<n>__add_column_xyz.sql`.

### Hibernate não cria as tabelas

**Esse comportamento é esperado!** Com `ddl-auto=validate`, Hibernate **NUNCA** cria tabelas. Quem cria é o **Flyway**. Verifique:

- Existe o arquivo `backend/src/main/resources/db/migration/V1__init.sql`?
- O log mostra `Successfully applied 1 migration`?
- O `application.properties` tem `spring.flyway.enabled=true`?

### `git status` mostra `.env` como untracked (deveria estar ignorado!)

Confere:
```powershell
git check-ignore -v .env
```

Se nada aparecer, o `.gitignore` não está pegando o `.env`. Verifica que:
- O `.gitignore` está na **raiz** do repo (não dentro de `backend/`)
- O nome é exatamente `.gitignore` (com ponto, sem extensão)
- A linha `.env` está lá

### Spring-dotenv não está lendo o `.env`

Confere que:
- `backend/.env` existe (não esqueça do ponto no nome)
- Você está rodando o Spring **a partir de `backend/`** (não de outra pasta)
- O `.env` não tem aspas em volta dos valores: `DB_PASSWORD=triagem` ✓ vs `DB_PASSWORD="triagem"` ❌

Pra testar, mude temporariamente `DB_NAME=triagem_teste` no `.env`. O Spring vai falhar tentando conectar em banco que não existe — isso prova que ele leu do `.env`.

---

## 16. Checklist final da F00-T01

Confira que você fez tudo:

### Arquivos criados/atualizados

- [ ] `docker-compose.yml` na raiz
- [ ] `.env` na raiz (com `POSTGRES_*`) — **não commitar**
- [ ] `.env.example` na raiz
- [ ] `backend/.env` (com `DB_*` e `APP_JWT_*`) — **não commitar**
- [ ] `backend/.env.example`
- [ ] `.gitignore` atualizado (com `.env` ignorado)
- [ ] `backend/pom.xml` corrigido (Postgres + Flyway + spring-dotenv)
- [ ] `backend/src/main/resources/application.properties` configurado
- [ ] `backend/src/main/resources/db/migration/V1__init.sql` criado

### Funcionalidades

- [ ] `docker compose up -d` sobe Postgres em healthy
- [ ] `./mvnw spring-boot:run` inicia sem erro
- [ ] Log mostra `Successfully applied 1 migration to schema "public"`
- [ ] Log mostra `HikariPool-1 - Start completed`
- [ ] Log mostra `Tomcat started on port 8080`
- [ ] `\dt` no Postgres mostra **8 tabelas** (7 + flyway_schema_history)
- [ ] `SELECT * FROM flyway_schema_history` mostra V1 com `success=t`
- [ ] `curl localhost:8080/` retorna 404 (esperado, sem endpoints ainda)
- [ ] `docker compose down` e `docker compose up -d` mantém os dados (Flyway não re-aplica)

### README atualizado (parte da task)

- [ ] README explica o passo `docker compose up -d` antes de subir o backend
- [ ] README aponta para esse tutorial pra quem nunca configurou

### Segurança

- [ ] `git status` **não** mostra `.env` ou `backend/.env` como untracked
- [ ] `.env.example` tem comentário falando "trocar em prod"
- [ ] Nenhuma senha real está hardcoded no `application.properties` ou `pom.xml`
- [ ] `ddl-auto=validate` (não `update`) — Flyway é dono do schema

---

## Próximos passos depois dessa task

1. **Atualizar o README** raiz com instruções (parte da F00-T01)
2. **Commitar** tudo numa branch `feature/F00-T01-setup-postgres-docker` e abrir PR
3. **Ir para F00-T02** (CORS) — agora que a base tá pronta
4. **Quando F03 for implementada,** criar `V2__seed_sintomas.sql` com os 9 sintomas
5. **Quando F04 for implementada,** criar `V3__seed_perguntas_<sintoma>.sql` para cada quiz

Se travou em algum ponto, volta no §15 (Troubleshooting) ou pede ajuda no Discord do time. **Não tente "consertar" deletando volume sem saber o que está fazendo** — você pode perder dados de outros devs.

---

**Última atualização:** 2026-05-06
**Autor:** Tutorial mentoria (PJI Triagem Pediátrica)
