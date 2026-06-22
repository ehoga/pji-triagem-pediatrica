# Fluxo da Triagem

## Visão geral

O fluxo de triagem permite que o usuário selecione múltiplos sintomas para uma mesma criança e responda o questionário de forma agrupada por sintoma.

Exemplo de sintomas escolhidos:

- febre
- tosse
- vômito

## Etapas do fluxo

1. O backend lista os sintomas disponíveis em `GET /symptoms`.
2. O frontend envia os sintomas escolhidos para `POST /assessments/questionnaire`.
3. O backend retorna o questionário agrupado por sintoma.
4. O frontend apresenta primeiro as perguntas de um sintoma, depois do próximo, até concluir todos.
5. O frontend envia todas as respostas para `POST /assessments`.
6. O backend calcula score, red flag e classificação por sintoma.
7. O backend calcula score total e classificação final da triagem.
8. O backend salva a triagem geral em `avaliacao` e o resultado por sintoma em `avaliacao_sintoma`.
9. Após salvar a avaliação, o frontend pode buscar orientações em `GET /assessments/{assessmentId}/orientation`.
10. O histórico da área logada usa as avaliações já persistidas em `GET /users/{userId}/assessments/history`.

## Questionário agrupado por sintoma

O endpoint `POST /assessments/questionnaire` recebe uma lista de `symptomIds` e retorna:

- dados básicos do sintoma;
- perguntas do sintoma;
- opções das perguntas do tipo `OPTIONS`.

O backend não devolve:

- `score`;
- `redFlag`;
- pesos de perguntas `YESNO`.

Esses dados ficam restritos ao backend para evitar acoplamento do frontend com a lógica clínica.

## Resposta por sintoma

Cada sintoma enviado em `POST /assessments` contém sua própria lista de respostas.

### Perguntas `OPTIONS`

O frontend deve enviar:

- `questionId`
- `optionId`

O backend:

- valida se a pergunta pertence ao sintoma;
- valida se a opção pertence à pergunta;
- soma `opcao_pergunta.score`;
- marca red flag quando `opcao_pergunta.red_flag = true`.

### Perguntas `YESNO`

O frontend deve enviar:

- `questionId`
- `answer`

Valores aceitos:

- `YES`
- `NO`
- `DUNNO`

O backend:

- valida se a pergunta pertence ao sintoma;
- busca `peso_yesno`;
- soma `score_yes`, `score_no` ou `score_dunno`;
- marca red flag conforme `red_flag_on_yes` ou `red_flag_on_no`.

## Cálculo por sintoma

Para cada sintoma:

- inicia `score = 0`;
- inicia `redFlagDetected = false`;
- percorre todas as respostas;
- calcula score final do sintoma;
- calcula classificação final do sintoma.

### Classificação por sintoma

- se `redFlagDetected = true` -> `HIGH`
- senão:
  - `score <= 4` -> `LOW`
  - `score <= 9` -> `MOD`
  - `score >= 10` -> `HIGH`

## Classificação final da triagem

- se qualquer sintoma for `HIGH` -> `HIGH`
- senão se qualquer sintoma for `MOD` -> `MOD`
- senão -> `LOW`

O `totalScore` da triagem é a soma dos scores de todos os sintomas respondidos.

## Persistência

### `avaliacao`

Representa a triagem geral:

- criança avaliada;
- score total;
- red flag geral;
- classificação final;
- resumo em JSONB;
- versão do protocolo.

### `avaliacao_sintoma`

Representa o resultado individual de cada sintoma:

- sintoma avaliado;
- score do sintoma;
- red flag do sintoma;
- classificação do sintoma;
- respostas processadas em JSONB.

## Endpoints envolvidos

- `GET /symptoms`
- `POST /assessments/questionnaire`
- `POST /assessments`
- `GET /assessments/{assessmentId}/orientation`
- `GET /users/{userId}/assessments/history`
