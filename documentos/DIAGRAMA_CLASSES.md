# Diagrama de Classes - EcoTrack Oracle API

## Visao geral

Este documento resume a estrutura de classes atual do projeto por camada, refletindo o estado atual do codigo.

## Diagrama de camadas (alto nivel)

```text
Clients (Mobile/Web/Postman)
        |
        v
Controllers
  - AuthController
  - MobileProductController
  - HistoryController
  - FavoriteController
  - HealthController
  - ProductController
  - UserController
  - ScanController
  - ImpactController
  - NutritionController
        |
        v
Services
  - AuthService
  - MobileProductService
  - MobileHistoryService
  - MobileFavoriteService
  - ProductService
  - UserService
  - ScanService
  - ImpactService
  - NutritionService
  - ExternalIdCodec
  - UserActivityEventPublisher
        |
        v
Repositories (Spring Data JPA)
  - ProductRepository
  - UserAccountRepository
  - ScanHistoryRepository
  - FavoriteRepository
  - ProductImpactRepository
  - ProductNutritionRepository
        |
        v
Domain (JPA)
  - Product
  - UserAccount
  - ScanHistory
  - Favorite
  - FavoriteId
  - ProductImpact
  - ProductNutrition
```

## Diagrama de dominio (entidades)

```mermaid
classDiagram

class UserAccount {
  UUID id
  String email
  String passwordHash
  String displayName
  OffsetDateTime createdAt
  OffsetDateTime updatedAt
}

class Product {
  UUID id
  String name
  String category
  BigDecimal kcal100g
  BigDecimal co2PerUnit
  String barcode
}

class ScanHistory {
  UUID id
  OffsetDateTime scannedAt
  String source
}

class Favorite {
  OffsetDateTime createdAt
}

class FavoriteId {
  UUID userId
  UUID productId
}

class ProductImpact {
  UUID productId
  BigDecimal co2PerUnit
  BigDecimal waterL
  String origin
  OffsetDateTime updatedAt
}

class ProductNutrition {
  UUID id
  String nutriKey
  String nutriValue
}

UserAccount "1" --> "0..*" ScanHistory : user
Product "1" --> "0..*" ScanHistory : product

UserAccount "1" --> "0..*" Favorite : user
Product "1" --> "0..*" Favorite : product
Favorite *-- FavoriteId : embedded id

Product "1" --> "0..1" ProductImpact : impact
Product "1" --> "0..*" ProductNutrition : nutrition
```

## Classes de integracao e infraestrutura

## Feign
- `InternalProductClient`
- `OpenFoodFactsClient`

## Seguranca
- `SecurityConfig`
- `JwtAuthenticationFilter`
- `JwtService`
- `CurrentUserService`
- `UserRoleResolver`
- `RoleType`
- `RestAuthenticationEntryPoint`
- `RestAccessDeniedHandler`

## Mensageria
- `RabbitMessagingConfig`
- `MessagingProperties`
- `UserActivityEventPublisher`
- `UserActivityEventListener`
- `UserActivityEvent`

## Observacoes

- Existem dois conjuntos de endpoints: mobile-friendly (sem `/api`) e legados/HATEOAS (com `/api`).
- A logica mobile usa IDs externos com prefixo (`prod-`, `user-`, `history-`, `favorite-`) via `ExternalIdCodec`.

