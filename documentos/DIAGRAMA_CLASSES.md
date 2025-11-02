# Diagrama de Classes - EcoTrack Oracle API

## Visão Geral da Arquitetura

A aplicação segue uma arquitetura em camadas (Layered Architecture) com separação clara de responsabilidades:

- **Controller Layer**: Endpoints REST
- **Service Layer**: Lógica de negócio
- **Repository Layer**: Acesso a dados
- **Domain Layer**: Entidades JPA
- **Representation Layer**: Modelos HATEOAS

## Diagrama de Classes de Entidade (Domain Layer)

```
┌─────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                       │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────┐
│           UserAccount               │
├─────────────────────────────────────┤
│ - id: UUID                          │
│ - email: String                     │
│ - passwordHash: String              │
│ - displayName: String               │
│ - createdAt: OffsetDateTime         │
│ - updatedAt: OffsetDateTime         │
└─────────────────────────────────────┘
                  │
                  │ 1
                  │
                  │ N
┌─────────────────┴─────────────────────────────────┐
│                                                 │
│ N                                         N     │
│                                            │     │
│  ┌─────────────────────┐    ┌──────────────────┐│
│  │   ScanHistory       │    │    Favorite      ││
│  ├─────────────────────┤    ├──────────────────┤│
│  │ - id: UUID          │    │ - id: FavoriteId ││
│  │ - user: UserAccount │    │ - user: UserAcc. ││
│  │ - product: Product  │    │ - product: Prod. ││
│  │ - scannedAt: ODT    │    │ - createdAt: ODT ││
│  │ - source: String    │    └──────────────────┘│
│  └─────────────────────┘                        │
│            │                                    │
│            │ N                                   │
│            │                                     │
┌────────────▼─────────────────────────────────────┐
│              Product                             │
├──────────────────────────────────────────────────┤
│ - id: UUID                                       │
│ - name: String                                   │
│ - category: String                               │
│ - kcal100g: BigDecimal                           │
│ - co2PerUnit: BigDecimal                         │
│ - barcode: String                                │
└────────────┬─────────────────────────────────────┘
             │
     ┌───────┼───────┐
     │ 1     │ 1     │ N
     │       │       │
┌────▼───┐ ┌─▼────┐ ┌▼───────────────────┐
│Product │ │Prod. │ │ ProductNutrition   │
│Impact  │ │Impact│ ├────────────────────┤
├────────┤ ├──────┤ │ - id: UUID         │
│-prodId │ │-prod │ │ - product: Product │
│-co2Per │ │-co2  │ │ - nutriKey: String │
│Unit    │ │PerU  │ │ - nutriValue: Str │
│-waterL │ │nit   │ └────────────────────┘
│-origin │ └──────┘
│-updAt  │
└────────┘

┌─────────────────────────────────────┐
│         FavoriteId                  │
│  (EmbeddedId - Chave Composta)      │
├─────────────────────────────────────┤
│ - userId: UUID                      │
│ - productId: UUID                   │
└─────────────────────────────────────┘
```

## Diagrama de Classes da Aplicação (Camadas)

```
┌─────────────────────────────────────────────────────────┐
│                  CONTROLLER LAYER                       │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────────┐  ┌──────────────────┐            │
│  │ ProductController│  │ UserController    │            │
│  ├──────────────────┤  ├──────────────────┤            │
│  │ + list()         │  │ + list()         │            │
│  │ + get(id)         │  │ + get(id)        │            │
│  │ + create(p)       │  │ + create(u)     │            │
│  │ + update(id, p)   │  │ + update(id, u) │            │
│  │ + delete(id)      │  │ + delete(id)    │            │
│  │ + byBarcode(code) │  │ + byEmail(email)│            │
│  │ + byCategory(n)   │  └──────────────────┤            │
│  │ + search(q)       │                          │
│  └──────────────────┘                          │
│            │                                    │
│  ┌─────────┼──────────────────┐                │
│  │         │                  │                │
│  │ ┌───────▼──────┐  ┌───────▼──────┐         │
│  │ │ ScanController│  │ImpactController│       │
│  │ │ NutritionCtrl│  └──────────────┘         │
│  │ └──────────────┘                            │
│  └─────────────────────────────────────────────┘
│                    │
│                    │ usa
│                    │
┌────────────────────▼─────────────────────────────┐
│              REPRESENTATION LAYER                 │
├──────────────────────────────────────────────────┤
│                                                  │
│  ┌──────────────────┐  ┌──────────────────┐    │
│  │ProductRepresent. │  │UserRepresentation│    │
│  ├──────────────────┤  ├──────────────────┤    │
│  │ + toModel(p):    │  │ + toModel(u):    │    │
│  │   EntityModel    │  │   EntityModel    │    │
│  └──────────────────┘  └──────────────────┘    │
│                                                  │
└──────────────────────────────────────────────────┘
                    │
                    │ usa
                    │
┌────────────────────▼─────────────────────────────┐
│               SERVICE LAYER                       │
├──────────────────────────────────────────────────┤
│                                                  │
│  ┌──────────────┐  ┌──────────────┐             │
│  │ProductService│  │ UserService  │             │
│  ├──────────────┤  ├──────────────┤             │
│  │ + list()      │  │ + list()     │             │
│  │ + get(id)     │  │ + get(id)    │             │
│  │ + upsert(p)   │  │ + create(u) │             │
│  │ + delete(id)  │  │ + update()   │             │
│  │ + findByBar() │  │ + findByEmail│             │
│  │ + byCategory() │  │ + delete(id) │             │
│  │ + search(q)   │  └──────────────┘             │
│  └──────────────┘                                │
│                                                  │
│  ┌──────────────┐  ┌──────────────┐             │
│  │ ScanService  │  │ImpactService │             │
│  │NutritionSvc  │  └──────────────┘             │
│  └──────────────┘                                │
│                    │                              │
│                    │ usa                          │
└────────────────────┼──────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────┐
│            REPOSITORY LAYER                       │
├──────────────────────────────────────────────────┤
│                                                  │
│  ┌──────────────┐  ┌──────────────┐            │
│  │ProductRepo   │  │ UserRepo     │            │
│  │ :JpaRepository│  │ :JpaRepository│           │
│  └──────────────┘  └──────────────┘            │
│                                                  │
│  ┌──────────────┐  ┌──────────────┐            │
│  │ ScanRepo     │  │ ImpactRepo   │            │
│  │ FavoriteRepo │  │ NutritionRepo│            │
│  └──────────────┘  └──────────────┘            │
│                                                  │
└──────────────────────────────────────────────────┘
                     │
                     │ mapeia
                     │
┌────────────────────▼─────────────────────────────┐
│             DOMAIN LAYER                         │
├──────────────────────────────────────────────────┤
│                                                  │
│  ┌──────────────┐  ┌──────────────┐            │
│  │ Product      │  │ UserAccount  │            │
│  │ @Entity      │  │ @Entity      │            │
│  └──────────────┘  └──────────────┘            │
│                                                  │
│  ┌──────────────┐  ┌──────────────┐            │
│  │ScanHistory   │  │ Favorite     │            │
│  │ProductImpact │  │ProductNutrition│           │
│  └──────────────┘  └──────────────┘            │
│                                                  │
└──────────────────────────────────────────────────┘
```

## Anotações JPA Utilizadas

### Product
```java
@Entity
@Table(name = "PRODUCTS")
- @Id @Column(columnDefinition = "RAW(16)")
- @NotBlank @Column(nullable = false)
```

### UserAccount
```java
@Entity
@Table(name = "USERS")
- @Id @Column(columnDefinition = "RAW(16)")
- @Email @Column(unique = true)
```

### ScanHistory
```java
@Entity
@Table(name = "SCAN_HISTORY")
- @ManyToOne @JoinColumn(name = "USER_ID")
- @ManyToOne @JoinColumn(name = "PRODUCT_ID")
```

### Favorite
```java
@Entity
@Table(name = "FAVORITES")
- @EmbeddedId
- @ManyToOne @MapsId("userId")
- @ManyToOne @MapsId("productId")
```

### ProductImpact
```java
@Entity
@Table(name = "PRODUCT_IMPACT")
- @Id @Column(columnDefinition = "RAW(16)")
- @OneToOne @JoinColumn
```

### ProductNutrition
```java
@Entity
@Table(name = "PRODUCT_NUTRITION")
- @Id @Column(columnDefinition = "RAW(16)")
- @ManyToOne @JoinColumn(name = "PRODUCT_ID")
```

## Padrões de Projeto Aplicados

1. **Repository Pattern**: Abstração do acesso aos dados via JpaRepository
2. **Service Layer Pattern**: Separação da lógica de negócio
3. **DTO/Representation Pattern**: EntityModel para HATEOAS
4. **Builder Pattern**: Lombok @Builder para construção de entidades
5. **Converter Pattern**: UuidRaw16Converter para conversão UUID ↔ RAW(16)

## Relacionamentos JPA

- **@OneToMany**: UserAccount → ScanHistory, Product → ProductNutrition
- **@ManyToOne**: ScanHistory → UserAccount, ScanHistory → Product
- **@ManyToMany**: UserAccount ↔ Product (via Favorite)
- **@OneToOne**: Product ↔ ProductImpact

