# Modelagem do Banco

## Decisão de migration

Foi criada uma migration V2 para ajustar a modelagem de avaliação. Embora o projeto esteja no início, não é seguro assumir que todos os bancos locais ou ambientes de desenvolvimento estejam vazios. Por isso, a V1 foi preservada e o ajuste estrutural ficou em `V2__ajusta_avaliacao_multiplos_sintomas.sql`.

O projeto mantém tipos de domínio como `VARCHAR`, sem enum nativo do PostgreSQL. Os enums existem apenas no Java.

## Tabelas existentes

### users

Guarda os usuários do sistema.

Campos principais:

- `id`;
- `nome`;
- `cpf`;
- `email`;
- `senha`;
- `tipo_usuario`;
- `criado_em`;
- `atualizado_em`;
- `ativo`;
- `bloqueado`;
- `bloqueado_temporariamente`;
- `tentativas`.

Relacionamentos:

- Um usuário pode ter várias crianças em `crianca`.

### crianca

Guarda as crianças vinculadas aos usuários/responsáveis.

Campos principais:

- `id`;
- `usuario_id`;
- `nome`;
- `cpf`;
- `data_nascimento`;
- `peso_kg`;
- `avatar_emoji`;
- `criado_em`.

Relacionamentos:

- `crianca.usuario_id` referencia `users(id)` com `ON DELETE CASCADE`.
- Uma criança pode ter várias avaliações em `avaliacao`.

### sintoma

Catálogo de sintomas avaliáveis.

Campos principais:

- `id`;
- `codigo`;
- `nome`;
- `descricao_curta`;
- `icone_ref`;
- `cor_hex`;
- `ordem`.

Relacionamentos:

- Um sintoma possui várias perguntas em `pergunta`.
- Um sintoma pode aparecer em vários resultados de `avaliacao_sintoma`.

### pergunta

Perguntas ligadas a cada sintoma.

Campos principais:

- `id`;
- `sintoma_id`;
- `codigo`;
- `texto`;
- `sub`;
- `tipo`;
- `ordem`.

Relacionamentos:

- `pergunta.sintoma_id` referencia `sintoma(id)` com `ON DELETE CASCADE`.
- Uma pergunta `OPTIONS` possui opções em `opcao_pergunta`.
- Uma pergunta `YESNO` possui pesos em `peso_yesno`.

### opcao_pergunta

Opções de resposta para perguntas do tipo `OPTIONS`.

Campos principais:

- `id`;
- `pergunta_id`;
- `codigo`;
- `texto`;
- `score`;
- `red_flag`;
- `ordem`.

Relacionamentos:

- `opcao_pergunta.pergunta_id` referencia `pergunta(id)` com `ON DELETE CASCADE`.

### peso_yesno

Pesos de perguntas do tipo `YESNO`.

Campos principais:

- `id`;
- `pergunta_id`;
- `score_yes`;
- `score_no`;
- `score_dunno`;
- `red_flag_on_yes`;
- `red_flag_on_no`.

Relacionamentos:

- `peso_yesno.pergunta_id` referencia `pergunta(id)` com `ON DELETE CASCADE`.

### avaliacao

Na V1, `avaliacao` representava uma avaliação presa a um único sintoma, porque continha `sintoma_id`, `classificacao`, `score` e `respostas`.

Na nova modelagem, `avaliacao` passa a representar a triagem geral.

Campos principais:

- `id`;
- `crianca_id`;
- `classificacao_final`;
- `score_total`;
- `red_flag_detected`;
- `respostas`;
- `protocolo_versao`;
- `criado_em`.

Relacionamentos:

- `avaliacao.crianca_id` referencia `crianca(id)` com `ON DELETE CASCADE`.
- Uma avaliação possui vários resultados por sintoma em `avaliacao_sintoma`.

## Nova tabela avaliacao_sintoma

`avaliacao_sintoma` representa o resultado calculado para cada sintoma dentro de uma avaliação geral.

Campos principais:

- `id`;
- `avaliacao_id`;
- `sintoma_id`;
- `classificacao`;
- `score`;
- `red_flag_detected`;
- `respostas`;
- `criado_em`.

Relacionamentos:

- `avaliacao_sintoma.avaliacao_id` referencia `avaliacao(id)` com `ON DELETE CASCADE`.
- `avaliacao_sintoma.sintoma_id` referencia `sintoma(id)`.

Índices:

- `avaliacao_sintoma(avaliacao_id)`;
- `avaliacao_sintoma(sintoma_id)`;
- `avaliacao(crianca_id, criado_em DESC)`;
- `avaliacao(classificacao_final)`.

## Resumo da nova modelagem

A tabela `avaliacao` guarda a triagem geral da criança, com score total, classificação final e red flag geral.

A tabela `avaliacao_sintoma` guarda o resultado individual de cada sintoma, com score, classificação, red flag e respostas detalhadas.

Com isso, a avaliação deixa de ser limitada a um sintoma único e passa a representar corretamente uma triagem com múltiplos sintomas.

## Relacionamento do fluxo de triagem

O desenho atual da triagem é:

- `users` -> possui várias `crianca`;
- `crianca` -> possui várias `avaliacao`;
- `avaliacao` -> possui várias `avaliacao_sintoma`;
- `avaliacao_sintoma` -> referencia um `sintoma`;
- `sintoma` -> possui várias `pergunta`;
- `pergunta` -> pode usar:
  - `opcao_pergunta`, quando o tipo é `OPTIONS`;
  - `peso_yesno`, quando o tipo é `YESNO`.

## Papel de cada tabela no cálculo

### `avaliacao`

`avaliacao` representa a triagem geral da criança. Ela consolida:

- score total da sessão;
- red flag geral;
- classificação final;
- resumo das respostas processadas;
- metadados da triagem.

### `avaliacao_sintoma`

`avaliacao_sintoma` representa o resultado calculado para cada sintoma dentro da triagem geral. Ela guarda:

- score do sintoma;
- classificação do sintoma;
- red flag detectada no sintoma;
- JSON com as respostas processadas daquele sintoma.

## Observação sobre persistência em JSONB

As respostas processadas são persistidas em JSONB no padrão do projeto usando `String` com JSON válido no lado Java.

Isso permite:

- auditoria do cálculo;
- rastreabilidade por sintoma;
- reconstrução da triagem sem depender apenas do score final.
