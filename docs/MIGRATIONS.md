# Estratégia de Migrations com Flyway

> Como o **PediTriagem** versiona, evolui e mantém o schema do banco de dados.
> Documento de design — para o setup operacional ver [`infra/SETUP.md`](../infra/SETUP.md).
> Para o modelo de dados conceitual ver [`ARQUITETURA.md` §6](./ARQUITETURA.md#6-modelo-de-dados-er-e-por-que-ele-é-assim).

## Sumário

1. [O problema: por que migrations explícitas](#1-o-problema-por-que-migrations-explícitas)
2. [Por que Flyway (e não outras opções)](#2-por-que-flyway-e-não-outras-opções)
3. [Setup no Spring Boot](#3-setup-no-spring-boot)
4. [Convenções de nomenclatura](#4-convenções-de-nomenclatura)
5. [Schema inicial — `V1__init.sql`](#5-schema-inicial--v1__initsql)
6. [Workflow do dev: adicionar uma migration](#6-workflow-do-dev-adicionar-uma-migration)
7. [Conflitos: branches paralelas com migrations](#7-conflitos-branches-paralelas-com-migrations)
8. [Migrations destrutivas: a regra forward-only](#8-migrations-destrutivas-a-regra-forward-only)
9. [Backfills: data migrations](#9-backfills-data-migrations)
10. [Testando migrations com Testcontainers](#10-testando-migrations-com-testcontainers)
11. [Deploy em produção: checklist por release](#11-deploy-em-produção-checklist-por-release)
12. [Quando NÃO usar Flyway](#12-quando-não-usar-flyway)

---

## 1. O problema: por que migrations explícitas

O `application.properties` atual do projeto vai usar `spring.jpa.hibernate.ddl-auto=update` durante F00. Isso é OK em dev local, mas **não pode ir para produção**. Razões:

| Risco | Exemplo concreto |
|---|---|
| **Mudanças silenciosas** | Renomeei `peso_kg` para `peso`. JPA cria coluna `peso` mas **não move dado** nem dropa `peso_kg`. Resultado: dado se duplica de forma inconsistente. |
| **Drops perigosos** | Removi um campo da entity. Hibernate (em modo `create-drop`) dropa a coluna em produção e **destrói dados** sem aviso. |
| **Diferença entre ambientes** | Dev local roda `update`, prod roda outra coisa, schema acaba **divergindo** entre ambientes — debug vira pesadelo. |
| **Sem rollback** | Algo deu errado depois do deploy? Não há histórico de o que mudou. |
| **Sem auditoria** | Daqui a 6 meses ninguém sabe **por que** a coluna `protocolo_versao` foi criada. |

**Migration explícita** resolve tudo isso porque é **código no Git, com mensagem de commit, autor, data e PR review**. Schema deixa de ser "magia do ORM" e vira **artefato versionado**.

**Princípio:** o schema do banco é tão crítico quanto o código da aplicação. Trate-o com o mesmo rigor.

---

## 2. Por que Flyway (e não outras opções)

| Opção | Veredicto | Motivo |
|---|---|---|
| **Flyway** | ✅ Recomendado | SQL puro, simples, integrado ao Spring Boot, padrão da indústria Java |
| **Liquibase** | ⚠ Possível | XML/YAML/JSON pesado. Mais features (rollback automático), mas curva maior. Vale para quem já conhece. |
| **JPA `ddl-auto=update`** | ❌ Não em prod | Já discutido na §1. Use só em dev. |
| **JPA `ddl-auto=validate`** | ✅ Em prod | Não cria nada, mas valida que entities batem com schema. **Use junto com Flyway.** |
| **Scripts SQL manuais** | ❌ Não | Quem garantiu que rodaram em todo ambiente? Quem versiona? |
| **Hibernate Schema Update Tool** | ❌ Não | Mesmo problema do `ddl-auto`. |

**Decisão final:**

- **Em dev** (local): `ddl-auto=validate` + Flyway aplica migrations
- **Em prod**: `ddl-auto=validate` + Flyway aplica migrations
- **Em testes**: Testcontainers + Flyway aplica migrations (mesma coisa que prod)

`validate` significa: "Hibernate, **não toque** no schema. Apenas confira no startup que a estrutura bate com as `@Entity`." Se houver divergência, a aplicação **falha ao subir**. Isso é exatamente o que queremos.

---

## 3. Setup no Spring Boot

### 3.1 Dependências (Maven)

Adicione em `backend/pom.xml`:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

(versão é gerenciada pelo BOM do Spring Boot)

### 3.2 Estrutura de pastas

```
backend/
└── src/
    └── main/
        └── resources/
            └── db/
                └── migration/
                    ├── V1__init.sql
                    ├── V2__seed_sintomas.sql
                    ├── V3__seed_perguntas_febre.sql
                    └── V4__add_protocolo_versao.sql
```

Flyway **automaticamente** acha tudo em `db/migration/` no classpath e roda em ordem na startup.

### 3.3 `application.properties`

```properties
# Validação estrita: schema deve bater com entities
spring.jpa.hibernate.ddl-auto=validate

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true

# Datasource
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:triagem}
spring.datasource.username=${DB_USER:triagem}
spring.datasource.password=${DB_PASSWORD:triagem}
```

`baseline-on-migrate=true`: necessário se vocês começarem com banco já populado. Para projeto novo (que é o caso de vocês), não importa.

`validate-on-migrate=true`: Flyway compara checksums das migrations já aplicadas com as do disco. Se alguém **alterou uma migration antiga** depois de aplicada, a aplicação **falha ao subir**. Isso é proteção sagrada.

### 3.4 application-dev.properties (perfil dev)

```properties
# Em dev, mostrar SQL gerado (debug)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Flyway pode ser desligado em dev se quiser ddl-auto=update temporariamente,
# mas recomendamos manter Flyway sempre ligado pra paridade com prod
```

---

## 4. Convenções de nomenclatura

Flyway usa um padrão estrito:

```
V<versão>__<descrição>.sql
```

| Parte | Regra |
|---|---|
| `V` | maiúsculo, sempre |
| versão | número inteiro, monotônico (1, 2, 3, ...). **NÃO** repete. |
| `__` | dois underscores |
| descrição | snake_case, descritivo, max 50 chars |
| extensão | `.sql` |

### Exemplos válidos

```
V1__init.sql
V2__seed_sintomas.sql
V3__add_index_avaliacao_crianca.sql
V4__add_column_protocolo_versao.sql
V5__rename_peso_to_peso_kg.sql
V100__add_table_log_auditoria.sql
```

### Exemplos inválidos

```
v1__init.sql              ❌ minúsculo
V1.0__init.sql            ❌ ponto no número (use só inteiro)
V1_init.sql               ❌ falta um underscore
V1__init.SQL              ❌ extensão maiúscula
V1__Init Schema.sql       ❌ espaço, CamelCase
```

### Versionamento por ano-mês (alternativa)

Times grandes às vezes usam timestamp para evitar conflitos de número:

```
V20260506_1430__add_protocolo_versao.sql
```

Para um time de 8 devs, **inteiros simples bastam**. O conflito é raro e fácil de resolver (ver §7).

### Tipos de migration

| Prefixo | Quando usar |
|---|---|
| `V` (Versioned) | Padrão. Roda **uma vez**, na ordem. |
| `R` (Repeatable) | Roda toda vez que o checksum muda. Útil para views/procedures. |
| `U` (Undo) | Migrations de reversão. **Não recomendado** — ver §8. |

---

## 5. Schema inicial — `V1__init.sql`

Esse é o schema completo do MVP, baseado no modelo conceitual em [`ARQUITETURA.md` §6](./ARQUITETURA.md#6-modelo-de-dados-er-e-por-que-ele-é-assim). Já incluí `protocolo_versao` em `avaliacao` (resolvendo a ressalva da §11.3 do ARQUITETURA).

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

-- Index GIN para queries futuras em respostas (opcional, custo de espaço)
-- CREATE INDEX idx_avaliacao_respostas_gin ON avaliacao USING GIN (respostas);
```

### 5.1 Insights de design embutidos no schema

- **Constraints de check no DB**, não só no código: email, datas, scores. **Make illegal states unrepresentable** — princípio aplicado.
- **`ON DELETE CASCADE`** em `crianca → usuario` e `pergunta → sintoma`. Em `avaliacao → crianca` também — embora discutido em [`ARQUITETURA.md` §6.1](./ARQUITETURA.md#61-decisões-interessantes-nesse-modelo) que soft delete seria preferível em saúde. Para MVP fica cascade.
- **Index composto** `(crianca_id, criado_em DESC)`: a query "histórico de uma criança ordenado por data" usa isso direto, sem sort.
- **JSONB em `avaliacao.respostas`**: índice GIN comentado (descomenta se um dia precisar de query agregada por resposta).
- **`protocolo_versao` já no schema inicial** — auditabilidade desde o dia 1.

### 5.2 Seeds são migrations separadas

**NÃO** colocar `INSERT INTO sintoma VALUES (...)` no `V1__init.sql`. Razões:

1. Schema e dado são preocupações diferentes. Se o seed mudar, a migration de schema não deveria.
2. Em testes às vezes você quer schema sem dado.
3. Seeds podem ser revisitados (`R__seed_sintomas.sql` repeatable).

Estrutura sugerida:

```
V1__init.sql                    ← só schema
V2__seed_sintomas.sql           ← os 9 sintomas
V3__seed_perguntas_febre.sql    ← quiz de febre (ref do design)
V4__seed_perguntas_tosse.sql
... (uma migration por sintoma)
```

Cada task `F04-T06` a `F04-T14` no plano vira uma migration `V?__seed_perguntas_<sintoma>.sql`. PR pequeno, isolado, fácil de revisar.

Exemplo de `V2__seed_sintomas.sql`:

```sql
INSERT INTO sintoma (codigo, nome, descricao_curta, icone_ref, cor_hex, ordem) VALUES
    ('fever',    'Febre',         'Temperatura corporal elevada',     'thermo',  '#F59E5C', 1),
    ('cough',    'Tosse',         'Tosse seca ou produtiva',          'cough',   '#7CC4F2', 2),
    ('vomit',    'Vômitos',       'Episódios de vômito',              'vomit',   '#A88BD9', 3),
    ('diarrhea', 'Diarreia',      'Evacuações líquidas frequentes',   'drop',    '#5BC0BE', 4),
    ('belly',    'Dor abdominal', 'Dor ou desconforto abdominal',     'belly',   '#F2A65A', 5),
    ('breath',   'Falta de ar',   'Dificuldade respiratória',         'lung',    '#EB5757', 6),
    ('rash',     'Manchas',       'Manchas ou erupções na pele',      'rash',    '#E2A52E', 7),
    ('trauma',   'Trauma leve',   'Quedas, batidas, escoriações',     'bandage', '#27AE60', 8),
    ('ear',      'Dor de ouvido', 'Dor ou incômodo no ouvido',        'ear',     '#9B72CB', 9);
```

---

## 6. Workflow do dev: adicionar uma migration

```
                  ┌──────────────────────────────────┐
                  │ 1. Eu preciso adicionar coluna   │
                  │    `telefone` em usuario         │
                  └──────────────┬───────────────────┘
                                 │
                                 ▼
                  ┌──────────────────────────────────┐
                  │ 2. git checkout dev && git pull  │
                  │    git checkout -b feature/...   │
                  └──────────────┬───────────────────┘
                                 │
                                 ▼
                  ┌──────────────────────────────────┐
                  │ 3. Verifico maior V*__ atual:    │
                  │    ls -1 src/main/resources/     │
                  │       db/migration/              │
                  │    → última é V12__...           │
                  │    → minha será V13              │
                  └──────────────┬───────────────────┘
                                 │
                                 ▼
                  ┌──────────────────────────────────┐
                  │ 4. Crio V13__add_telefone_       │
                  │      usuario.sql                 │
                  │                                  │
                  │   ALTER TABLE usuario            │
                  │     ADD COLUMN telefone          │
                  │       VARCHAR(20);               │
                  └──────────────┬───────────────────┘
                                 │
                                 ▼
                  ┌──────────────────────────────────┐
                  │ 5. Atualizo a entity Usuario.java│
                  │    com @Column(name="telefone")  │
                  └──────────────┬───────────────────┘
                                 │
                                 ▼
                  ┌──────────────────────────────────┐
                  │ 6. mvn test                      │
                  │   (Testcontainers sobe Postgres  │
                  │    novo, aplica migrations,      │
                  │    valida com ddl-auto=validate) │
                  └──────────────┬───────────────────┘
                                 │
                                 ▼
                  ┌──────────────────────────────────┐
                  │ 7. PR. Reviewer confere:         │
                  │   - SQL faz sentido              │
                  │   - Entity bate                  │
                  │   - Sem DROP destrutivo          │
                  │   - Constraint quando aplicável  │
                  └──────────────────────────────────┘
```

---

## 7. Conflitos: branches paralelas com migrations

**Problema clássico:** dois devs trabalhando em paralelo:

- Dev A cria `V13__add_telefone.sql` na branch `feature/perfil-telefone`
- Dev B cria `V13__add_avatar_admin.sql` na branch `feature/admin-avatar`

Quem mergear primeiro fica com V13. Quem chegar segundo **PRECISA renumerar** sua migration para V14 antes de mergear.

### Política do time

> **Quem mergeia depois renomeia.** O segundo PR vê que V13 já existe na `dev`, faz `git checkout dev && git pull && git rebase dev`, e renomeia o arquivo de migration para o próximo número livre. Resolve o conflito e re-pusha.

### Por que NÃO usar timestamps por padrão

Tentação: "se a versão for `V20260506_1430`, nunca há conflito!" Verdade. Mas:

- Strings longas, mais difícil de conversar ("rode a migration V20260506_1430")
- Ordem de aplicação fica determinada pelo timestamp de criação, não pela ordem em que foram mergeadas. Pode causar pegadinha se alguém merga uma migration "antiga" depois de uma "nova".
- Para 8 devs, conflito acontece talvez 1x por sprint. Custo baixo.

**Use inteiros simples**. Renumerar é fácil.

### Como prevenir conflito

- Anuncie no canal do time: "vou criar V13"
- PRs com migration ganham label `migration` e prioridade de revisão
- Tech lead back coordena ordem se 2 PRs competirem

---

## 8. Migrations destrutivas: a regra forward-only

### O princípio

> **Migrations só vão pra frente. Nunca rode UNDO em produção.**

Por que? Porque dado é precioso. Se a `V13__drop_coluna_x.sql` foi para prod e destruiu dados, você **não recupera** rodando uma "V13_undo.sql". Você recupera de **backup**.

Então qual o protocolo? **Fix-forward**: você cria uma `V14` que **conserta** o problema:

```sql
-- V14__readd_coluna_x.sql
ALTER TABLE foo ADD COLUMN x VARCHAR(255);
-- e algum backfill se for o caso
```

### A consequência prática: pense duas vezes antes de DROP

| Ação | Risco | Recomendação |
|---|---|---|
| `CREATE TABLE` | Baixo | OK direto |
| `ADD COLUMN` (nullable) | Baixo | OK direto |
| `ADD COLUMN NOT NULL DEFAULT ...` | Médio | OK, mas em tabelas grandes pode locker. Use 2 passos: add nullable + backfill + alter to not null |
| `CREATE INDEX` | Médio (lock) | Use `CREATE INDEX CONCURRENTLY` em prod |
| `DROP COLUMN` | **Alto** | Faça em **2 deploys** — ver §8.1 |
| `DROP TABLE` | **Alto** | Idem, e tenha backup verificado |
| `RENAME COLUMN` | **Alto** | Faça em **3 deploys** — ver §8.2 |
| `ALTER TYPE` | **Alto** | Idem, particularmente em ENUMs |

### 8.1 Padrão "expand and contract" para DROP

Não dropar coluna no mesmo deploy que removeu o uso dela. Em vez disso:

```
Deploy 1 (expand):
  - código não usa mais a coluna
  - schema ainda tem a coluna
  → coluna fica órfã, mas dado intacto. Se rollback, código volta a funcionar.

Deploy 2 (contract), só depois de Deploy 1 estável por 1+ semana:
  - V_n__drop_coluna_x.sql
```

Esse padrão se chama **expand and contract** ou **parallel change**. É a forma de evoluir schema sem janela de manutenção.

### 8.2 Padrão para RENAME

`RENAME COLUMN` em produção é traiçoeiro porque o código fica um instante referenciando o nome antigo enquanto o schema já mudou. Em deploy contínuo, **3 fases**:

```
Deploy 1: ADD COLUMN nova; código escreve em ambas, lê da antiga
Deploy 2: backfill; código lê da nova, ainda escreve em ambas
Deploy 3: DROP COLUMN antiga; código só usa a nova
```

Para um MVP com janelas de manutenção (downtime aceitável), você pode fazer `ALTER TABLE foo RENAME COLUMN a TO b` num único deploy com app desligada. Mas anote: **só funciona se você desligar a app**.

---

## 9. Backfills: data migrations

Às vezes a migration precisa **preencher dados**, não só mudar estrutura. Exemplo: você adicionou `protocolo_versao` em `avaliacao`. Linhas antigas têm `NULL`. Como popular?

### Opção A: DEFAULT no schema (preferido)

```sql
ALTER TABLE avaliacao
    ADD COLUMN protocolo_versao VARCHAR(20) NOT NULL DEFAULT '1.0.0';
```

Funciona em uma linha. Postgres preenche todas as linhas existentes com `'1.0.0'`. **Problema:** em tabela grande (milhões de linhas), pode demorar e segurar lock.

### Opção B: backfill explícito em duas etapas

```sql
-- V14__add_protocolo_versao.sql
ALTER TABLE avaliacao ADD COLUMN protocolo_versao VARCHAR(20);
UPDATE avaliacao SET protocolo_versao = '1.0.0' WHERE protocolo_versao IS NULL;
ALTER TABLE avaliacao ALTER COLUMN protocolo_versao SET NOT NULL;
ALTER TABLE avaliacao ALTER COLUMN protocolo_versao SET DEFAULT '1.0.0';
```

Mais SQL, mais controle. Para o tamanho de banco do MVP, Opção A basta.

### Opção C: backfill em código (Java)

Para lógica complexa de backfill (ex: derivar campo a partir de outro):

```java
@Component
public class V15BackfillProtocoloVersao implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        // só roda se houver linhas pra backfillar
        // idempotente: pode rodar várias vezes sem problema
    }
}
```

Em geral **prefira SQL puro em migration**. Backfill em código tem mais coisa pra dar errado e é mais difícil de testar.

---

## 10. Testando migrations com Testcontainers

O setup do projeto usa Testcontainers (decisão F08). Isso significa que **toda execução de teste sobe um Postgres novo, vazio, e Flyway aplica todas as migrations do zero**.

### O que isso garante

- Toda nova migration é testada em **fresh Postgres** antes de ir pra prod
- Se uma migration tem erro de sintaxe ou referencia tabela que não existe, **CI falha**
- Se entity Java diverge do schema (`ddl-auto=validate`), **CI falha**

### Estrutura de teste

```java
@Testcontainers
@SpringBootTest
class FlywayMigrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("triagem_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void contextLoads() {
        // se subiu sem erro, todas as migrations rodaram com sucesso
        // e ddl-auto=validate confirmou que entities batem
    }
}
```

### Teste específico de uma migration

Para uma migration crítica (ex: rename de coluna com backfill), faça um teste explícito:

```java
@Test
void v14_backfill_populates_existing_avaliacoes() {
    // dado: insere avaliação ANTES da migration (rodando V13)
    // quando: aplica V14
    // então: linha existente tem protocolo_versao='1.0.0'
}
```

Para isso, use **Flyway programaticamente** com `target=V13` e depois `target=V14`. É possível, mas é setup avançado — só se a migration for muito crítica.

---

## 11. Deploy em produção: checklist por release

Toda release nova com migration nova **passa por esse checklist**:

### Antes de fazer a tag

- [ ] CI verde no PR (Testcontainers aplicou tudo do zero, sem erro)
- [ ] Migration foi revisada por outro dev (label `migration` no PR)
- [ ] Migration é **idempotente** ou claramente marcada como destrutiva
- [ ] Para DROP: rodando em ambiente de staging por pelo menos 24h
- [ ] Backup do Postgres de prod foi feito **manualmente** antes do deploy

### Durante o deploy

- [ ] Tag `vX.Y.Z` criada
- [ ] Workflow `deploy-prod` aprovado manualmente
- [ ] Logs do Flyway no startup: `Successfully applied N migrations`
- [ ] Healthcheck passa (`/health` retorna 200)

### Depois do deploy

- [ ] `docker compose exec postgres psql -U triagem triagem -c "SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;"` mostra a nova migration como `success=t`
- [ ] Smoke test manual da feature relacionada
- [ ] Backup automatizado pós-deploy (cron já cobre)

### Se algo der errado

1. **Não tente reverter a migration.** Sério. A regra é forward-only.
2. Avalie: é dado corrompido ou só comportamento errado?
3. **Se dado corrompido:** restaure do backup pré-deploy.
4. **Se comportamento errado:** crie `V_n+1__fix_xyz.sql` e novo deploy.
5. Comunique no Discord/Slack o que aconteceu — incidente, não vergonha.

---

## 12. Quando NÃO usar Flyway

Casos legítimos para **fugir** do Flyway:

| Caso | Use o quê |
|---|---|
| Operação de uma vez, em prod, urgente, sem tempo de release | `psql` manual no servidor + migration "post-fact" para reconciliar (com `flyway baseline`) |
| Schema de tabela temporária (test setup) | `@Sql` do Spring Test, não Flyway |
| DDL gerado dinamicamente (multi-tenant com schema-per-tenant) | Estratégia híbrida — Flyway no schema "core", DDL programático nos schemas de tenant |
| Tunning de índice em prod (`CREATE INDEX CONCURRENTLY`) | Pode rodar fora do Flyway, mas marque com `flyway repair` para registrar |

Para o MVP de vocês, **nenhum desses casos se aplica**. Flyway resolve 100%.

---

## Resumo executivo

1. **Adicionem Flyway no F00**, junto com a configuração de Postgres. É 30min de trabalho que evita 3 horas de incêndio futuro.
2. **`ddl-auto=validate` sempre** (dev, staging, prod). Nunca deixe Hibernate mexer no schema.
3. **Uma feature de schema = uma migration = um PR**. Pequeno, revisável, atômico.
4. **Forward-only.** Errou? Crie `V_n+1` que conserta.
5. **Migrations destrutivas (DROP, RENAME) viram 2-3 deploys** (expand and contract).
6. **Seeds são migrations separadas** do schema.
7. **Toda migration roda em fresh Postgres no CI** — Testcontainers garante.
8. **Backup verificado antes de toda release com migration nova.**

A migration `V1__init.sql` da §5 está pronta para vocês colarem em `backend/src/main/resources/db/migration/V1__init.sql` e tocarem o projeto. Os seeds (`V2`+) seguem com o trabalho do F03/F04.

---

**Última atualização:** 2026-05-06
**Autor:** Análise arquitetural sênior (mentoria)
