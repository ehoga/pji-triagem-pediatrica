# Orientações da Triagem

## Objetivo

Após a criação de uma triagem, o frontend pode buscar um conjunto de orientações baseado:

- na classificação final da avaliação;
- nos sintomas avaliados naquela triagem.

Essas orientações são usadas para montar a tela de resultado e de cuidados seguintes.

## Tabela `orientacao`

A tabela `orientacao` guarda textos configuráveis para a etapa de orientação.

Campos principais:

- `id`;
- `sintoma_id`;
- `classificacao`;
- `tipo`;
- `titulo`;
- `descricao`;
- `ordem`;
- `ativo`;
- `criado_em`.

## Orientação geral x orientação por sintoma

### Orientação geral

Usa `sintoma_id = NULL`.

Serve para mensagens amplas da classificação final, por exemplo:

- resumo do risco;
- ação principal;
- cuidados gerais;
- quando procurar ajuda;
- disclaimer.

### Orientação por sintoma

Usa `sintoma_id` preenchido.

Serve para complementar a resposta final com alertas ou cuidados mais específicos de um sintoma presente na triagem, por exemplo:

- febre alta em bebê pequeno;
- dificuldade para respirar;
- hidratação em diarreia ou vômitos.

## Tipos de orientação

Os tipos cadastrados hoje são:

- `SUMMARY`
- `MAIN_ACTION`
- `WARNING_SIGN`
- `HOME_CARE`
- `WHEN_SEEK_HELP`
- `DISCLAIMER`

No banco esses valores ficam em `VARCHAR`. Os enums existem apenas no Java.

## Endpoint

`GET /assessments/{assessmentId}/orientation`

Fluxo:

1. Busca a avaliação por `assessmentId`.
2. Busca os sintomas salvos em `avaliacao_sintoma`.
3. Usa a `classificacao_final` da avaliação.
4. Busca orientações gerais ativas daquela classificação.
5. Busca orientações específicas ativas dos sintomas presentes na avaliação.
6. Agrupa o resultado por tipo.

## Regras de montagem

- O `title` principal usa a primeira orientação do tipo `SUMMARY`.
- O `description` principal usa a mesma orientação `SUMMARY`.
- Se não houver `SUMMARY`, o backend usa fallback:
  - `LOW` -> `Baixo risco`
  - `MOD` -> `Risco moderado`
  - `HIGH` -> `Alto risco`
- O `disclaimer` usa a primeira orientação do tipo `DISCLAIMER`.
- Se não houver `DISCLAIMER`, o backend retorna:
  - `Este aplicativo não substitui avaliação médica profissional.`

## Exemplo de response

```json
{
  "assessmentId": 10,
  "childId": 1,
  "childName": "Lucas",
  "finalClassification": "HIGH",
  "title": "Alto risco",
  "description": "A triagem identificou sinais que exigem avaliação médica imediata.",
  "mainActions": [
    {
      "id": 13,
      "title": "Procure atendimento imediatamente",
      "description": "Procure atendimento imediatamente.",
      "type": "MAIN_ACTION",
      "symptomId": null,
      "symptomName": null
    }
  ],
  "warningSigns": [
    {
      "id": 14,
      "title": "Sinais de alerta",
      "description": "Sinais de alerta foram identificados na triagem.",
      "type": "WARNING_SIGN",
      "symptomId": null,
      "symptomName": null
    },
    {
      "id": 18,
      "title": "Dificuldade para respirar",
      "description": "Dificuldade para respirar é sinal de alerta.",
      "type": "WARNING_SIGN",
      "symptomId": 6,
      "symptomName": "Falta de ar"
    }
  ],
  "homeCare": [
    {
      "id": 15,
      "title": "Cuidados enquanto busca atendimento",
      "description": "Não deixe a criança sozinha e observe respiração, consciência e hidratação.",
      "type": "HOME_CARE",
      "symptomId": null,
      "symptomName": null
    }
  ],
  "whenSeekHelp": [
    {
      "id": 16,
      "title": "Quando procurar ajuda",
      "description": "Procure uma unidade de urgência ou emergência.",
      "type": "WHEN_SEEK_HELP",
      "symptomId": null,
      "symptomName": null
    }
  ],
  "symptoms": [
    {
      "symptomId": 1,
      "symptomName": "Febre",
      "classification": "HIGH",
      "score": 16,
      "redFlagDetected": true
    }
  ],
  "disclaimer": "Este aplicativo não substitui avaliação médica profissional."
}
```
