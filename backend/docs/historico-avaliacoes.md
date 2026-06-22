# Histórico de Avaliações

## Endpoint

`GET /users/{userId}/assessments/history`

Filtro opcional:

`GET /users/{userId}/assessments/history?childId=1`

Por enquanto o `userId` é recebido pela URL. No futuro esse contexto pode sair de `/me` usando o usuário do token JWT.

## Regra quando `childId` é nulo

Quando `childId` não é informado:

- o backend busca as crianças do usuário;
- retorna todas as avaliações de todas as crianças desse usuário;
- soma o total de avaliações;
- soma quantas avaliações ficaram em `LOW`, `MOD` e `HIGH`;
- mantém a lista de crianças para o frontend continuar exibindo o filtro.

## Regra quando `childId` é informado

Quando `childId` é informado:

- o backend valida se a criança pertence ao usuário;
- retorna apenas as avaliações daquela criança;
- calcula o total e os contadores de risco somente daquela criança;
- continua retornando a lista completa de crianças do usuário para o filtro.

## Dados retornados

O response possui:

- totais agregados da seleção atual;
- `selectedChildId`;
- lista de crianças do usuário;
- lista de avaliações ordenadas da mais recente para a mais antiga.

Cada item da lista de avaliações retorna:

- `id`;
- `childId`;
- `childName`;
- lista de nomes de sintomas;
- `createdAt`;
- `classification`.

## Exemplo de response

```json
{
  "totalAssessments": 8,
  "lowRiskCount": 4,
  "moderateRiskCount": 2,
  "highRiskCount": 2,
  "selectedChildId": null,
  "children": [
    {
      "id": 1,
      "name": "Lucas"
    },
    {
      "id": 2,
      "name": "Maria"
    }
  ],
  "assessments": [
    {
      "id": 10,
      "childId": 1,
      "childName": "Lucas",
      "symptoms": ["Febre", "Tosse"],
      "createdAt": "2026-06-18T14:30:00",
      "classification": "HIGH"
    },
    {
      "id": 9,
      "childId": 2,
      "childName": "Maria",
      "symptoms": ["Vômitos"],
      "createdAt": "2026-06-17T10:20:00",
      "classification": "LOW"
    }
  ]
}
```

## Observações de implementação

- Os sintomas do histórico são montados a partir de `avaliacao_sintoma`.
- A resposta não expõe entidades diretamente.
- O endpoint usa DTOs e mantém a estrutura pensada para a tela de histórico.
