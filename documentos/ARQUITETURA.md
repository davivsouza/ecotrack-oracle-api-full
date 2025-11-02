# Arquitetura da Aplicação - EcoTrack Oracle API

## Visão Geral

A aplicação **EcoTrack Oracle API** segue uma arquitetura em camadas (Layered Architecture) com separação clara de responsabilidades, facilitando manutenção, testes e evolução do sistema.

## Diagrama de Arquitetura de Alto Nível

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          CAMADA DE APRESENTAÇÃO                          │
│                                                                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                  │
│  │   Frontend   │  │  Mobile App  │  │  Postman/    │                  │
│  │  (React/Vue) │  │ (Android/iOS)│  │  Insomnia    │                  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘                  │
│         │                 │                  │                           │
│         └─────────────────┼──────────────────┘                           │
│                           │                                              │
└───────────────────────────┼──────────────────────────────────────────────┘
                            │
                            │ HTTP/REST
                            │
┌───────────────────────────▼──────────────────────────────────────────────┐
│                    CAMADA DE APLICAÇÃO (Spring Boot)                     │
│                                                                           │
│  ┌────────────────────────────────────────────────────────────┐         │
│  │                    CONTROLLER LAYER                        │         │
│  │  (Endpoints REST com HATEOAS Nível 3)                       │         │
│  │                                                             │         │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐       │         │
│  │  │  Product     │ │    User      │ │     Scan     │       │         │
│  │  │ Controller   │ │  Controller  │ │  Controller  │       │         │
│  │  └──────┬───────┘ └──────┬───────┘ └──────┬───────┘       │         │
│  │         │                │                │                 │         │
│  │  ┌──────▼───────┐ ┌──────▼───────┐                        │         │
│  │  │   Impact     │ │  Nutrition   │                        │         │
│  │  │ Controller   │ │  Controller  │                        │         │
│  │  └──────────────┘ └──────────────┘                        │         │
│  └─────────────────────────────┬─────────────────────────────┘         │
│                                │                                         │
│  ┌─────────────────────────────▼─────────────────────────────┐         │
│  │              REPRESENTATION LAYER (HATEOAS)                │         │
│  │                                                             │         │
│  │  ┌──────────────────┐  ┌──────────────────┐              │         │
│  │  │   Product        │  │   User            │              │         │
│  │  │  Representation  │  │  Representation  │              │         │
│  │  │  (EntityModel)   │  │  (EntityModel)    │              │         │
│  │  └──────────────────┘  └──────────────────┘              │         │
│  │                                                             │         │
│  │  Responsável por adicionar links HATEOAS aos recursos      │         │
│  └─────────────────────────────┬─────────────────────────────┘         │
│                                │                                         │
│  ┌─────────────────────────────▼─────────────────────────────┐         │
│  │                    SERVICE LAYER                           │         │
│  │              (Lógica de Negócio)                          │         │
│  │                                                             │         │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐     │         │
│  │  │  Product     │ │    User       │ │     Scan     │     │         │
│  │  │  Service     │ │   Service     │ │   Service    │     │         │
│  │  └──────┬───────┘ └──────┬───────┘ └──────┬───────┘     │         │
│  │         │                │                │               │         │
│  │  ┌──────▼───────┐ ┌──────▼───────┐                        │         │
│  │  │   Impact     │ │  Nutrition   │                        │         │
│  │  │   Service     │ │   Service    │                        │         │
│  │  └──────────────┘ └──────────────┘                        │         │
│  │                                                             │         │
│  │  Validações de negócio, regras e transformações            │         │
│  └─────────────────────────────┬─────────────────────────────┘         │
│                                │                                         │
│  ┌─────────────────────────────▼─────────────────────────────┐         │
│  │                  REPOSITORY LAYER                          │         │
│  │              (Acesso a Dados - JPA)                       │         │
│  │                                                             │         │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐     │         │
│  │  │  Product     │ │    User      │ │  ScanHistory │     │         │
│  │  │ Repository   │ │  Repository  │ │  Repository  │     │         │
│  │  └──────────────┘ └───────────────┘ └──────────────┘     │         │
│  │                                                             │         │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐     │         │
│  │  │  Favorite    │ │   Impact     │ │  Nutrition   │     │         │
│  │  │  Repository  │ │  Repository  │ │  Repository  │     │         │
│  │  └──────────────┘ └───────────────┘ └──────────────┘     │         │
│  │                                                             │         │
│  │  Abstração do acesso aos dados via Spring Data JPA         │         │
│  └─────────────────────────────┬─────────────────────────────┘         │
│                                │                                         │
└────────────────────────────────┼─────────────────────────────────────────┘
                                 │
                                 │ JDBC/JPA
                                 │
┌─────────────────────────────────▼─────────────────────────────────────────┐
│                      CAMADA DE PERSISTÊNCIA                               │
│                                                                           │
│  ┌────────────────────────────────────────────────────────────┐        │
│  │                   ORACLE DATABASE                            │        │
│  │                                                               │        │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │        │
│  │  │   USERS      │  │  PRODUCTS    │  │ SCAN_HISTORY │       │        │
│  │  │              │  │              │  │              │       │        │
│  │  │ - ID (UUID)  │  │ - ID (UUID)  │  │ - ID (UUID)  │       │        │
│  │  │ - EMAIL      │  │ - NAME       │  │ - USER_ID    │       │        │
│  │  │ - PASSWORD   │  │ - CATEGORY   │  │ - PRODUCT_ID│       │        │
│  │  │ - DISPLAY    │  │ - BARCODE    │  │ - SCANNED_AT │       │        │
│  │  │ - CREATED_AT │  │ - KCAL_100G  │  │ - SOURCE     │       │        │
│  │  │ - UPDATED_AT │  │ - CO2_PER_U  │  │              │       │        │
│  │  └──────────────┘  └──────┬───────┘  └──────────────┘       │        │
│  │                           │                                  │        │
│  │         ┌─────────────────┼─────────────────┐           │        │
│  │         │                 │                     │           │        │
│  │  ┌──────▼──────┐  ┌───────▼────────┐  ┌───────▼─────────┐ │        │
│  │  │   FAVORITES │  │ PRODUCT_IMPACT │  │PRODUCT_NUTRITION│ │        │
│  │  │             │  │                │  │                 │ │        │
│  │  │ - USER_ID   │  │ - PRODUCT_ID  │  │ - ID (UUID)     │ │        │
│  │  │ - PRODUCT_ID│  │ - CO2_PER_UNIT │  │ - PRODUCT_ID    │ │        │
│  │  │ - CREATED_AT│  │ - WATER_L      │  │ - NUTRI_KEY    │ │        │
│  │  └─────────────┘  │ - ORIGIN      │  │ - NUTRI_VALUE  │ │        │
│  │                    │ - UPDATED_AT  │  └─────────────────┘ │        │
│  │                    └────────────────┘                      │        │
│  │                                                               │        │
│  │  Armazenamento: UUID como RAW(16) para otimização            │        │
│  └───────────────────────────────────────────────────────────────┘        │
│                                                                           │
└───────────────────────────────────────────────────────────────────────────┘
```

## Arquitetura em Camadas - Detalhamento

### 1. Camada de Apresentação (Clientes)

**Responsabilidades:**
- Interface com o usuário final
- Consumo da API REST
- Navegação através de links HATEOAS

**Tecnologias:**
- Frontend Web (React, Vue, Angular)
- Aplicativos Mobile (Android/iOS)
- Ferramentas de Teste (Postman, Insomnia)

### 2. Camada Controller (REST API)

**Responsabilidades:**
- Receber requisições HTTP
- Validar dados de entrada
- Orquestrar chamadas para Service Layer
- Retornar respostas com HATEOAS

**Principais Classes:**
- `ProductController`: Gerencia endpoints de produtos
- `UserController`: Gerencia endpoints de usuários
- `ScanController`: Gerencia escaneamentos e favoritos
- `ImpactController`: Gerencia dados de impacto ambiental
- `NutritionController`: Gerencia informações nutricionais

**Características:**
- Implementa HATEOAS Nível 3 (Richardson Maturity Model)
- Retorna `EntityModel` e `CollectionModel` com links
- Validação usando Jakarta Validation

### 3. Camada de Representação (HATEOAS)

**Responsabilidades:**
- Adicionar links hipermidiáticos aos recursos
- Encapsular lógica de construção de links
- Facilitar navegação na API

**Principais Classes:**
- `ProductRepresentation`: Adiciona links a produtos
- `UserRepresentation`: Adiciona links a usuários

**Links Implementados:**
- `self`: Link para o próprio recurso
- `collection`: Link para a coleção
- `impact`: Link para dados de impacto
- `nutrition`: Link para informações nutricionais
- `scan-history`: Link para histórico de escaneamentos
- `favorites`: Link para favoritos do usuário

### 4. Camada de Serviço (Business Logic)

**Responsabilidades:**
- Implementar regras de negócio
- Coordenar operações entre repositories
- Validar regras específicas do domínio
- Transformar dados quando necessário

**Principais Classes:**
- `ProductService`: Lógica de negócio para produtos
- `UserService`: Lógica de negócio para usuários
- `ScanService`: Lógica de negócio para escaneamentos
- `ImpactService`: Lógica de negócio para impacto
- `NutritionService`: Lógica de negócio para nutrição

### 5. Camada de Repositório (Data Access)

**Responsabilidades:**
- Abstrair acesso ao banco de dados
- Implementar consultas customizadas quando necessário
- Gerenciar transações

**Principais Classes:**
- Interfaces estendendo `JpaRepository<T, ID>`
- Métodos customizados usando `@Query` quando necessário

**Repositories:**
- `ProductRepository`
- `UserAccountRepository`
- `ScanHistoryRepository`
- `FavoriteRepository`
- `ProductImpactRepository`
- `ProductNutritionRepository`

### 6. Camada de Domínio (Entities)

**Responsabilidades:**
- Representar entidades de negócio
- Mapeamento ORM (JPA/Hibernate)
- Validações de domínio

**Principais Classes:**
- `Product`: Entidade de produto
- `UserAccount`: Entidade de usuário
- `ScanHistory`: Entidade de histórico de escaneamento
- `Favorite`: Entidade de favorito (chave composta)
- `ProductImpact`: Entidade de impacto ambiental
- `ProductNutrition`: Entidade de informação nutricional

**Características:**
- Anotações JPA para mapeamento
- UUID como chave primária (convertido para RAW(16))
- Relacionamentos definidos via `@ManyToOne`, `@OneToOne`, etc.

### 7. Camada de Persistência (Database)

**Responsabilidades:**
- Armazenar dados persistentes
- Manter integridade referencial
- Otimizar consultas

**Banco de Dados:**
- Oracle Database
- UUIDs armazenados como RAW(16)
- Constraints de integridade referencial
- Índices para otimização

## Fluxo de Dados - Exemplo: Buscar Produto

```
1. Cliente (Frontend/Mobile)
   ↓ HTTP GET /api/products/{id}
   
2. ProductController.get(id)
   ↓ Chama Service
   
3. ProductService.get(id)
   ↓ Chama Repository
   
4. ProductRepository.findById(id)
   ↓ Consulta Database
   
5. Oracle Database
   ↓ Retorna dados
   
6. ProductRepository
   ↓ Retorna Entity
   
7. ProductService
   ↓ Retorna Product
   
8. ProductRepresentation.toModel(product)
   ↓ Adiciona links HATEOAS
   
9. ProductController
   ↓ Retorna EntityModel<Product>
   
10. Cliente recebe JSON com dados + links
```

## Padrões Arquiteturais Aplicados

1. **Layered Architecture**: Separação clara de responsabilidades em camadas
2. **Repository Pattern**: Abstração do acesso a dados
3. **Service Layer Pattern**: Encapsulamento da lógica de negócio
4. **DTO/Representation Pattern**: Separação entre modelos de domínio e representação
5. **Dependency Injection**: Inversão de controle via Spring
6. **RESTful API**: Arquitetura baseada em recursos e métodos HTTP
7. **HATEOAS**: Hypermedia como engine da aplicação

## Tecnologias e Frameworks

- **Java 17**: Linguagem de programação
- **Spring Boot 3.3.3**: Framework principal
- **Spring Data JPA**: Abstração de acesso a dados
- **Spring HATEOAS**: Implementação de hypermedia
- **Hibernate**: ORM (Object-Relational Mapping)
- **Oracle Database**: Banco de dados relacional
- **Lombok**: Redução de boilerplate
- **Jakarta Validation**: Validação de dados
- **OpenAPI/Swagger**: Documentação automática da API

## Escalabilidade e Performance

- **Conversão UUID → RAW(16)**: Otimização de armazenamento no Oracle
- **Índices**: Criados automaticamente nas chaves primárias e foreign keys
- **Lazy Loading**: Configurado via JPA para otimizar consultas
- **Connection Pooling**: Gerenciado pelo Spring Boot
- **Caching**: Pode ser adicionado na camada de Service/Repository

## Segurança (Considerações Futuras)

- Autenticação JWT
- Autorização baseada em roles
- Validação de entrada rigorosa
- Proteção contra SQL Injection (JPA)
- HTTPS em produção

## Evoluções da Sprint 2

1. **HATEOAS Nível 3**: Implementação completa de hypermedia
2. **Camada de Representação**: Separação da lógica de links
3. **Refatoração de Controllers**: Uso consistente de EntityModel e CollectionModel
4. **Documentação de Arquitetura**: Este documento detalhado

