# Contexto do Backend

## Visão geral

O backend é uma aplicação Spring Boot 3.3.4 com Java 21. O projeto usa Spring Web, Spring Security, Spring Data JPA, Flyway, PostgreSQL, Lombok, ModelMapper, Querydsl, JWT e Springdoc/OpenAPI.

O schema do banco é controlado pelo Flyway, e o Hibernate está configurado com `spring.jpa.hibernate.ddl-auto=validate`. Isso significa que as entidades Java precisam bater com as migrations, mas o Hibernate não cria nem altera tabelas automaticamente.

## Estrutura principal

Os pacotes atuais ficam abaixo de `com.pji.triagem`.

- `base`: infraestrutura compartilhada do projeto.
- `base.config.security`: configuração de segurança, filtro JWT e handlers de autenticação.
- `base.dto`: DTOs genéricos de resposta e autenticação.
- `base.filter`: filtro que lê o token JWT e injeta a autenticação no contexto do Spring Security.
- `base.provider`: geração e leitura de tokens JWT.
- `base.repository`: `BaseRepository`, base para repositories Spring Data JPA.
- `base.service`: contratos genéricos, incluindo `BaseService`, `ResponseService` e autenticação JWT.
- `base.service.impl`: implementações base, incluindo `BaseServiceImpl`.
- `base.utils`: utilitários de autenticação, parsing, documentos, datas e strings.
- `base.validator`: validações de CPF/CNPJ.
- `config`: propriedades da aplicação e CORS.
- `controller`: controllers REST.
- `dto.request`: DTOs de entrada.
- `dto.response`: DTOs de saída.
- `exception`: exceções de negócio e validação.
- `factory`: factories de usuário, login e validação de documentos.
- `handler`: handler global de exceções.
- `model`: entidades JPA e enums de domínio.
- `repository`: repositories Spring Data.
- `service`: interfaces de service.
- `service.impl`: implementações de service.
- `service.impl.auth`: componentes específicos de autenticação.

## Usuário e autenticação

A entidade `User` mapeia a tabela `users`. Ela usa campos Java em inglês e colunas do banco em português, por exemplo `name -> nome`, `password -> senha`, `type -> tipo_usuario`, `createdAt -> criado_em`.

O enum `TypeUser` define os tipos `ADMIN`, `DOCTOR` e `USER`. No banco, o tipo é persistido como texto por `@Enumerated(EnumType.STRING)`, sem enum nativo PostgreSQL.

O fluxo atual de autenticação é:

- `AuthController` expõe `/auth/login` e `/auth/register/client`.
- `AuthServiceImpl` autentica com `AuthenticationManager`, reseta tentativas de login e gera tokens.
- `CustomAuthProvider` valida CPF, senha e tipo do usuário.
- `CustomUserDetailsService` carrega o usuário e monta `UserAuth`.
- `JwtTokenProvider` cria e interpreta tokens JWT.
- `SecurityFilter` lê o token `Bearer`, valida o JWT e coloca um `BaseAuthDTO` no `SecurityContext`.
- `SecurityConfig` libera rotas de autenticação, Swagger e Actuator, e exige autenticação nas demais rotas.

Nas rotas autenticadas, o usuário atual pode ser identificado pelo id salvo no principal do `SecurityContext`.

## Padrão controller, service e repository

O padrão encontrado no módulo de usuário é:

- Controller REST injeta services e `ResponseService`.
- Requests e responses passam por DTOs.
- Service de domínio estende a interface genérica `BaseService<T>`.
- Implementação do service estende `BaseServiceImpl<T>`.
- Repository estende `BaseRepository<T, Long>`.

A interface genérica usada pelo usuário é `com.pji.triagem.base.service.BaseService`. A implementação base é `com.pji.triagem.base.service.impl.BaseServiceImpl`.

`BaseServiceImpl` já fornece:

- `find(Long id)`;
- `findAll(Pageable pageable)`;
- `save(T entity)`;
- `update(Long id, T entity)`;
- busca com `Predicate` do Querydsl.

Cada implementação concreta informa seu repository via `getRepository()` e o nome do recurso via `getResourceName()`.

## Exceptions

As exceções de domínio ficam em `com.pji.triagem.exception`. As mais usadas são:

- `ValidationException`: erros de regra de negócio ou entrada inválida.
- `ResourceNotFoundException`: recurso não encontrado.
- `InformationNotFoundException`: usado pela implementação genérica quando `find` não encontra entidade.
- `InvalidLoginException`, `InvalidTokenException` e `BlockedUserException`: fluxo de autenticação.

`ApplicationExceptionHandler` converte exceções em `ResponseDTO` com lista de erros.

## Flyway e migrations

As migrations ficam em `src/main/resources/db/migration`.

Atualmente existe `V1__init.sql`, que cria:

- `users`;
- `crianca`;
- `sintoma`;
- `pergunta`;
- `opcao_pergunta`;
- `peso_yesno`;
- `avaliacao`.

A migration V1 usa nomes de tabelas e colunas em português, tipos textuais como `VARCHAR(30)` para classificações/tipos e `JSONB` para respostas da avaliação. O banco é PostgreSQL, mas o projeto evita enums nativos e deixa os enums apenas no Java.
