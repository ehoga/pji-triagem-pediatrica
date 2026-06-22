# Planejamento da Triagem

## Regra de negócio planejada

A triagem pediátrica será representada por uma avaliação geral, vinculada a uma criança. Uma criança pertence a um usuário/responsável, e a avaliação só pode ser criada para crianças do usuário autenticado, exceto quando o usuário for `ADMIN`.

Uma avaliação pode conter vários sintomas. Cada sintoma avaliado possui seu próprio conjunto de respostas, score, classificação e indicação de red flag.

## Estrutura da triagem

- Uma criança pertence a um usuário/responsável.
- Uma avaliação/triagem pertence a uma criança.
- Uma avaliação/triagem pode conter vários sintomas.
- Cada sintoma possui perguntas cadastradas.
- Cada pergunta pertence a um sintoma.
- Cada pergunta pode ser do tipo `YESNO` ou `OPTIONS`.
- Perguntas `OPTIONS` usam opções cadastradas em `opcao_pergunta`.
- Perguntas `YESNO` usam pesos cadastrados em `peso_yesno`.

## Cálculo de score

Para perguntas `OPTIONS`, o backend busca a opção escolhida em `opcao_pergunta` e soma o campo `score`. A red flag da resposta vem do campo `opcao_pergunta.red_flag`.

Para perguntas `YESNO`, o backend busca o registro em `peso_yesno` e soma:

- `YES`: `score_yes`;
- `NO`: `score_no`;
- `DUNNO`: `score_dunno`.

Para perguntas `YESNO`, a red flag vem de:

- `YES`: `red_flag_on_yes`;
- `NO`: `red_flag_on_no`;
- `DUNNO`: não gera red flag.

## Classificação por sintoma

A classificação por sintoma deve ser calculada assim:

- se houver red flag, classificar como `HIGH`;
- se não houver red flag e `score <= 4`, classificar como `LOW`;
- se não houver red flag e `score <= 9`, classificar como `MOD`;
- se não houver red flag e `score >= 10`, classificar como `HIGH`.

Red flag sempre eleva a classificação do sintoma para alta gravidade.

## Classificação final

A avaliação geral deve guardar:

- score total;
- classificação final;
- se houve red flag;
- resumo/respostas em JSONB, quando útil;
- versão do protocolo;
- data de criação.

A classificação final deve ser:

- `HIGH`, se qualquer sintoma avaliado tiver classificação `HIGH`;
- `MOD`, se nenhum sintoma for `HIGH` e pelo menos um for `MOD`;
- `LOW`, se todos os sintomas forem `LOW`.

O score total é a soma dos scores de todos os sintomas avaliados.

## Dados persistidos

A avaliação geral fica em `avaliacao`. Cada resultado por sintoma fica em `avaliacao_sintoma`.

Cada `avaliacao_sintoma` deve guardar:

- sintoma avaliado;
- score do sintoma;
- classificação do sintoma;
- se houve red flag naquele sintoma;
- respostas usadas no cálculo.

Esse desenho permite uma triagem com múltiplos sintomas sem perder o detalhe de cálculo e auditoria por sintoma.
