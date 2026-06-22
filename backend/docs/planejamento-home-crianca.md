# Planejamento Home e Cadastro de Criança

## Objetivo

Organizar a base do backend para os próximos endpoints da tela inicial e da tela de cadastro da criança, evitando retorno direto de entidades nos controllers.

## Home

A tela inicial precisa listar as crianças de um usuário.

Neste momento, o `userId` será recebido pela URL:

- `GET /users/{userId}/children`

No futuro, esse fluxo deve migrar para um endpoint baseado no usuário autenticado:

- `GET /me/children`

Nessa evolução, o usuário será resolvido pelo token JWT, sem necessidade de receber `userId` pela URL.

## Dados esperados na listagem da home

Cada criança retornada para a home deve conter:

- `id`
- `name`
- `age`
- `ageInMonths`
- `avatarEmoji`
- `totalAssessments`
- `lastAssessmentAt`

## Cadastro da criança

A tela de cadastro precisa criar uma criança vinculada a um usuário.

Neste momento, o vínculo será feito usando o `userId` da URL:

- `POST /users/{userId}/children`

No futuro, o vínculo deve passar a usar o usuário autenticado via JWT.

## Diretriz arquitetural

- Controllers não devem retornar entidades JPA.
- A partir desta tarefa, o módulo de criança deve usar DTOs de request/response.
- A conversão entre entidade e DTO deve usar MapStruct.
- Campos derivados, como `age`, `ageInMonths`, `totalAssessments` e `lastAssessmentAt`, devem ser preenchidos no service.
