# Seed de Sintomas Base

## Objetivo

Esta seed popula o catálogo inicial de sintomas e um questionário base para testes do fluxo completo de triagem no backend.

Ela foi construída a partir dos sintomas exibidos no protótipo em `design/peditriagem-screens-a.jsx`, mantendo nomes, descrições curtas, cores e referências de ícone compatíveis com a interface desenhada.

## Sintomas criados

- `FEBRE` -> `Febre` -> ícone `thermo` -> cor `#F59E5C`
- `TOSSE` -> `Tosse` -> ícone `cough` -> cor `#56CCF2`
- `VOMITOS` -> `Vômitos` -> ícone `vomit` -> cor `#9B7BE0`
- `DIARREIA` -> `Diarreia` -> ícone `drop` -> cor `#56CCF2`
- `DOR_ABDOMINAL` -> `Dor abdominal` -> ícone `belly` -> cor `#F2C94C`
- `FALTA_DE_AR` -> `Falta de ar` -> ícone `lung` -> cor `#EB5757`
- `MANCHAS_PELE` -> `Manchas na pele` -> ícone `rash` -> cor `#E18ABF`
- `TRAUMA_LEVE` -> `Trauma leve` -> ícone `bandage` -> cor `#7BC393`
- `DOR_OUVIDO` -> `Dor de ouvido` -> ícone `ear` -> cor `#56CCF2`

## Perguntas por sintoma

### FEBRE

- `FEBRE_TEMPERATURA` (`OPTIONS`)
- `FEBRE_MAIS_3_DIAS` (`YESNO`)
- `FEBRE_BEBE_MENOR_3_MESES` (`YESNO`)

### TOSSE

- `TOSSE_INTENSIDADE` (`OPTIONS`)
- `TOSSE_COM_FEBRE` (`YESNO`)
- `TOSSE_CHIADO` (`YESNO`)

### VÔMITOS

- `VOMITOS_FREQUENCIA` (`OPTIONS`)
- `VOMITOS_DESIDRATACAO` (`YESNO`)

### DIARREIA

- `DIARREIA_FREQUENCIA` (`OPTIONS`)
- `DIARREIA_SANGUE` (`YESNO`)

### DOR ABDOMINAL

- `DOR_ABD_INTENSIDADE` (`OPTIONS`)
- `DOR_ABD_BARRIGA_DURA` (`YESNO`)

### FALTA DE AR

- `FALTA_AR_INTENSIDADE` (`OPTIONS`)
- `FALTA_AR_LABIOS_ROXOS` (`YESNO`)

### MANCHAS NA PELE

- `MANCHAS_PELE_ASPECTO` (`OPTIONS`)
- `MANCHAS_PELE_NAO_DESAPARECEM` (`YESNO`)

### TRAUMA LEVE

- `TRAUMA_LEVE_LESAO` (`OPTIONS`)
- `TRAUMA_LEVE_CONFUSAO` (`YESNO`)

### DOR DE OUVIDO

- `DOR_OUVIDO_INTENSIDADE` (`OPTIONS`)
- `DOR_OUVIDO_SECRECAO` (`YESNO`)

## Como `OPTIONS` e `YESNO` funcionam

Perguntas `OPTIONS` usam registros em `opcao_pergunta`. Cada opção tem:

- `codigo`;
- `texto`;
- `score`;
- `red_flag`.

Quando o usuário escolhe uma opção:

- o backend soma o `score`;
- se `red_flag = true`, o sintoma já sobe para classificação `HIGH`.

Perguntas `YESNO` usam um registro em `peso_yesno`. Esse registro define:

- `score_yes`;
- `score_no`;
- `score_dunno`;
- `red_flag_on_yes`;
- `red_flag_on_no`.

Quando o usuário responde:

- `YES` soma `score_yes`;
- `NO` soma `score_no`;
- `DUNNO` soma `score_dunno`;
- `DUNNO` não ativa red flag por padrão.

## Exemplos de resultado

### Exemplo LOW

Sintoma `FEBRE`:

- `FEBRE_TEMPERATURA` -> `MENOR_37_8` -> score `0`
- `FEBRE_MAIS_3_DIAS` -> `NO` -> score `0`
- `FEBRE_BEBE_MENOR_3_MESES` -> `NO` -> score `0`

Resultado:

- score do sintoma `0`
- red flag `false`
- classificação `LOW`

### Exemplo MOD

Sintoma `TOSSE`:

- `TOSSE_INTENSIDADE` -> `FREQUENTE` -> score `5`
- `TOSSE_COM_FEBRE` -> `DUNNO` -> score `2`
- `TOSSE_CHIADO` -> `NO` -> score `0`

Resultado:

- score `7`
- red flag `false`
- classificação `MOD`

### Exemplo HIGH

Sintoma `FALTA_DE_AR`:

- `FALTA_AR_INTENSIDADE` -> `COM_ESFORCO`

Resultado:

- score `9`
- red flag `true`
- classificação `HIGH`

Mesmo sem atingir score alto, qualquer resposta com red flag força `HIGH`.

## Observações

- Os scores foram simplificados para fins acadêmicos e de teste automatizado.
- A seed busca cobrir o fluxo técnico de seleção de sintomas, montagem de questionário e cálculo de classificação.
- Esses valores não representam protocolo clínico validado.
- A triagem gerada pelo sistema não substitui avaliação médica real.
