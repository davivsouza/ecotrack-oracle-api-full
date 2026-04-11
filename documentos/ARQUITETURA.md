# Arquitetura da Aplicacao - EcoTrack Oracle API

## Visao geral

A API foi implementada com Spring Boot em arquitetura em camadas, com separacao clara entre:
- Entrada HTTP (controllers)
- Regras de negocio (services)
- Persistencia (repositories + entidades JPA)
- Seguranca JWT
- Integracoes sincrona (Feign) e assincrona (RabbitMQ)

O projeto atende dois grupos de rotas:
- Rotas legadas/HATEOAS em `/api/**`
- Rotas de app mobile em `/auth`, `/products`, `/history`, `/favorites`, `/health`

## Camadas da aplicacao

## 1) Controller layer

Classes principais:
- `AuthController` (`/auth`)
- `MobileProductController` (`/products`)
- `HistoryController` (`/history`)
- `FavoriteController` (`/favorites`)
- `HealthController` (`/health`)
- `ProductController` (`/api/products`)
- `UserController` (`/api/users`)
- `ScanController` (`/api/scan`)
- `ImpactController` (`/api/impact`)
- `NutritionController` (`/api/nutrition`)

Responsabilidades:
- Receber request/response
- Validar payloads com Bean Validation (`@Valid`)
- Delegar regras para services

## 2) Service layer

Classes principais:
- `AuthService`: cadastro/login/me + emissao de JWT
- `MobileProductService`: busca local, importacao OpenFoodFacts e score nutricional/ambiental
- `MobileHistoryService`: CRUD de historico com validacao via Feign + publicacao de evento
- `MobileFavoriteService`: CRUD de favoritos com validacao via Feign + publicacao de evento
- `ProductService`, `UserService`, `ScanService`, `ImpactService`, `NutritionService`
- `ExternalIdCodec`: converte IDs internos UUID para IDs externos (`prod-...`, `user-...`, etc)

Responsabilidades:
- Regras de negocio
- Validacoes de integridade
- Orquestracao entre persistencia e integracoes

## 3) Repository layer

Repositorios Spring Data JPA:
- `ProductRepository`
- `UserAccountRepository`
- `ScanHistoryRepository`
- `FavoriteRepository`
- `ProductImpactRepository`
- `ProductNutritionRepository`

Responsabilidades:
- CRUD e consultas por criterio
- Paginacao nas rotas `/api/products` e `/api/users`

## 4) Domain layer

Entidades JPA:
- `UserAccount`
- `Product`
- `ScanHistory`
- `Favorite` + `FavoriteId` (chave composta)
- `ProductImpact`
- `ProductNutrition`

Observacao:
- O projeto possui `UuidRaw16Converter` auto-aplicado para UUID, alinhado ao cenario Oracle.

## Seguranca

A autenticacao/autorizacao esta implementada com:
- `SecurityConfig`
- `JwtAuthenticationFilter`
- `JwtService`
- `CurrentUserService`
- `UserRoleResolver`

Perfis:
- `ROLE_USER`
- `ROLE_ADMIN`

Regras de acesso relevantes:
- Publico: `/health`, `/products/**`, `/auth/register`, `/auth/login`, Swagger
- Autenticado (`USER` ou `ADMIN`): `/auth/me`, `/history/**`, `/favorites/**`, `/api/scan/**`, `/api/products/**` (leitura), `/api/impact/**`, `/api/nutrition/**`
- Somente `ADMIN`: alteracoes em `/api/products/**` (POST/PUT/DELETE) e `/api/users/**`

## Integracoes

## 1) Feign (sincrono)

- `InternalProductClient`: chama a propria API (`/products/{id}`) para validar/obter produto no fluxo mobile
- `OpenFoodFactsClient`: integra com `https://world.openfoodfacts.org/api/v2`

Uso pratico:
- `MobileHistoryService` e `MobileFavoriteService` validam produto via Feign antes de criar/atualizar
- `MobileProductService` importa produto externo por codigo de barras quando nao existe localmente

## 2) Mensageria RabbitMQ (assincrono)

- Configuracao: `RabbitMessagingConfig` + `MessagingProperties`
- Publicacao: `UserActivityEventPublisher`
- Consumo: `UserActivityEventListener`
- Chave de config: `ecotrack.messaging.*` (ativacao por `MESSAGING_ENABLED=true`)

Eventos publicados nos fluxos mobile:
- `HISTORY_CREATED`, `HISTORY_UPDATED`, `HISTORY_DELETED`
- `FAVORITE_CREATED`, `FAVORITE_UPDATED`, `FAVORITE_DELETED`

## Persistencia e ambientes

Configuracoes principais:
- `application.properties`: Oracle (padrao), JWT, Feign, mensageria
- `application-postgres.properties`: profile `postgres` para execucao local com Docker
- `schema-postgres.sql`: cria schema local para desenvolvimento

Observacao importante:
- O endpoint `/health` usa `SELECT 1 FROM DUAL`, adequado ao Oracle.
- Em Postgres, o campo `database` da resposta pode aparecer `down` mesmo com app de pe.

## Fluxos principais da Sprint 3

1. Autenticacao completa:
- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`

2. Consulta/importacao de produto por barcode:
- `GET /products/barcode/{barcode}`
- Busca local e, se necessario, importacao via OpenFoodFacts
- Enriquecimento com nutricao/impacto e calculo de scores

3. Historico/Favoritos com validacao sincrona + evento assincrono:
- `POST/PATCH/DELETE /history`
- `POST/PATCH/DELETE /favorites`
- Validacao de produto via Feign
- Publicacao de evento de atividade via RabbitMQ

## Tecnologias

- Java 17
- Spring Boot 3.3.3
- Spring Data JPA
- Spring Security
- Spring Cloud OpenFeign
- Spring AMQP (RabbitMQ)
- Spring HATEOAS
- OpenAPI/Swagger
- Oracle Database / PostgreSQL (profile local)

