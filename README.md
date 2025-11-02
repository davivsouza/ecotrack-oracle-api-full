# EcoTrack Oracle API

## 📋 Sobre o Projeto

O **EcoTrack Oracle API** é uma aplicação Spring Boot que permite rastrear o impacto ambiental e nutricional de produtos através de código de barras. A aplicação conecta-se ao Oracle Database e oferece funcionalidades para usuários registrarem escaneamentos de produtos, gerenciar favoritos e consultar informações sobre sustentabilidade e nutrição.

## 👥 Equipe de Desenvolvimento

| Nome | RM |
|------|----|------------------|
| Davi Vasconcelos Souza | 559906 
| Gustavo Dantas Oliveira | 560685 
| Paulo Neto | 560262




## 🎯 Público-Alvo

- **Consumidores conscientes** que desejam tomar decisões de compra mais sustentáveis
- **Empresas de varejo** interessadas em oferecer transparência sobre seus produtos
- **Organizações ambientais** que precisam de dados sobre impacto ecológico
- **Desenvolvedores** que querem integrar funcionalidades de sustentabilidade em suas aplicações

## 🚀 Como Executar a Aplicação

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+
- Oracle Database (configurado com as credenciais fornecidas)
- IDE (IntelliJ IDEA, Eclipse ou VS Code)

### Passos para Execução

1. **Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/ecotrack-oracle-api-full.git
cd ecotrack-oracle-api-full
```

2. **Configure o banco de dados:**
   - Edite o arquivo `src/main/resources/application.properties`
   - Verifique se as credenciais do Oracle estão corretas

3. **Execute a aplicação:**
```bash
mvn spring-boot:run
```

4. **Acesse a documentação:**
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs: http://localhost:8080/v3/api-docs


## 🏗️ Arquitetura da Aplicação

### Diagrama de Arquitetura Simplificado

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Mobile App    │    │   Postman       │
│   (React/Vue)   │    │   (Android/iOS) │    │   (Testes)      │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │    EcoTrack Oracle API    │
                    │    (Spring Boot 3.3.3)    │
                    │                           │
                    │  ┌─────────────────────┐ │
                    │  │ Controller Layer     │ │
                    │  │ (REST + HATEOAS)     │ │
                    │  └──────────┬───────────┘ │
                    │             │             │
                    │  ┌──────────▼───────────┐ │
                    │  │ Representation Layer │ │
                    │  │ (HATEOAS Links)      │ │
                    │  └──────────┬───────────┘ │
                    │             │             │
                    │  ┌──────────▼───────────┐ │
                    │  │ Service Layer        │ │
                    │  │ (Business Logic)      │ │
                    │  └──────────┬───────────┘ │
                    │             │             │
                    │  ┌──────────▼───────────┐ │
                    │  │ Repository Layer     │ │
                    │  │ (JPA/Hibernate)       │ │
                    │  └──────────┬───────────┘ │
                    └─────────────┼───────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │     Oracle Database       │
                    │   (RAW(16) UUID Storage)  │
                    └───────────────────────────┘
```

### Documentação Completa de Arquitetura

Para visualizar o **diagrama de arquitetura detalhado** com todas as camadas, componentes e fluxos de dados, consulte:

📄 **[Documentação Completa de Arquitetura](documentos/ARQUITETURA.md)**

### Camadas da Aplicação

A aplicação segue uma **arquitetura em camadas (Layered Architecture)** com as seguintes responsabilidades:

1. **Controller Layer**: Endpoints REST para comunicação HTTP, implementando HATEOAS nível 3
2. **Representation Layer**: Adição de links hipermidiáticos aos recursos (HATEOAS)
3. **Service Layer**: Lógica de negócio, validações e orquestração
4. **Repository Layer**: Acesso aos dados com Spring Data JPA
5. **Domain Layer**: Entidades JPA com mapeamento ORM
6. **Database Layer**: Oracle Database com armazenamento otimizado

### Fluxo de Requisição

```
Cliente → Controller → Representation → Service → Repository → Database
         (HTTP/REST)   (HATEOAS)     (Business)  (JPA)       (Oracle)
```

## 📊 Modelo de Dados

### Diagrama Entidade Relacionamento (DER)

O modelo de dados consiste em 6 entidades principais relacionadas:

```
┌─────────────────┐
│     USERS       │
├─────────────────┤
│ ID (PK)         │──┐
│ EMAIL (UK)      │  │
│ PASSWORD_HASH   │  │
│ DISPLAY_NAME    │  │
│ CREATED_AT      │  │
│ UPDATED_AT      │  │
└─────────────────┘  │
                     │
        ┌────────────┘
        │
        │ 1
        │
        │ N
┌───────▼───────────┐      ┌──────────────┐
│   SCAN_HISTORY     │──────│  PRODUCTS    │
├───────────────────┤ N   1 ├──────────────┤
│ ID (PK)           │◄─────┤ ID (PK)      │
│ USER_ID (FK)      │      │ NAME         │
│ PRODUCT_ID (FK)   │      │ CATEGORY     │
│ SCANNED_AT        │      │ KCAL_100G    │
│ SOURCE            │      │ CO2_PER_UNIT │
└───────────────────┘      │ BARCODE      │
                           └──────┬───────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          │                       │                       │
          │ 1                     │ 1                     │ N
          │                       │                       │
┌─────────▼─────────┐  ┌─────────▼──────────┐  ┌────────▼───────────┐
│ PRODUCT_IMPACT    │  │    FAVORITES       │  │ PRODUCT_NUTRITION  │
├───────────────────┤  ├────────────────────┤  ├────────────────────┤
│ PRODUCT_ID (PK/FK)│  │ USER_ID (PK/FK)    │  │ ID (PK)            │
│ CO2_PER_UNIT      │  │ PRODUCT_ID (PK/FK) │  │ PRODUCT_ID (FK)    │
│ WATER_L           │  │ CREATED_AT         │  │ NUTRI_KEY          │
│ ORIGIN            │  └────────────────────┘  │ NUTRI_VALUE        │
│ UPDATED_AT        │                          └────────────────────┘
└───────────────────┘
```

**Relacionamentos:**
- **USERS → SCAN_HISTORY**: 1:N (Um usuário pode ter vários escaneamentos)
- **PRODUCTS → SCAN_HISTORY**: 1:N (Um produto pode ser escaneado várias vezes)
- **USERS ↔ PRODUCTS**: N:M via FAVORITES (Tabela intermediária com chave composta)
- **PRODUCTS → PRODUCT_IMPACT**: 1:1 (Um produto tem um impacto ambiental)
- **PRODUCTS → PRODUCT_NUTRITION**: 1:N (Um produto pode ter várias informações nutricionais)

**Constraints:**
- USERS.EMAIL é único
- Todas as foreign keys respeitam integridade referencial
- UUIDs são armazenados como RAW(16) no Oracle

### Diagrama de Classes de Entidade

![Diagrama de Classes](documentos/DIAGRAMA_CLASSES.md)

As entidades principais incluem:
- **UserAccount**: Representa usuários do sistema
- **Product**: Representa produtos cadastrados
- **ScanHistory**: Histórico de escaneamentos por usuário
- **Favorite**: Relacionamento N:M entre usuários e produtos favoritos
- **ProductImpact**: Dados de impacto ambiental por produto
- **ProductNutrition**: Informações nutricionais por produto

Para mais detalhes, consulte o arquivo `documentos/DIAGRAMA_CLASSES.md`.

## 🔗 Endpoints da API

A API implementa **HATEOAS nível 3** (Richardson Maturity Model), retornando links hipermidiáticos em todas as respostas.

### 📦 Produtos (`/api/products`)
| Método | Endpoint | Descrição | Parâmetros de Paginação |
|--------|----------|-----------|-------------------------|
| GET | `/api/products` | Lista todos os produtos | `page`, `size`, `sort` |
| GET | `/api/products/{id}` | Busca produto por ID | - |
| GET | `/api/products/barcode/{code}` | Busca produto por código de barras | - |
| GET | `/api/products/category/{name}` | Lista produtos por categoria | `page`, `size`, `sort` |
| GET | `/api/products/search?q={term}` | Busca produtos por nome | `page`, `size`, `sort` |
| POST | `/api/products` | Cria novo produto | - |
| PUT | `/api/products/{id}` | Atualiza produto existente | - |
| DELETE | `/api/products/{id}` | Remove produto | - |

**Exemplo de paginação:**
```bash
GET /api/products?page=0&size=10&sort=name,asc
```

### 👤 Usuários (`/api/users`)
| Método | Endpoint | Descrição | Parâmetros de Paginação |
|--------|----------|-----------|-------------------------|
| GET | `/api/users` | Lista todos os usuários | `page`, `size`, `sort` |
| GET | `/api/users/{id}` | Busca usuário por ID | - |
| GET | `/api/users/by-email?email={email}` | Busca usuário por email | - |
| POST | `/api/users` | Cria novo usuário | - |
| PUT | `/api/users/{id}` | Atualiza usuário existente | - |
| DELETE | `/api/users/{id}` | Remove usuário | - |

**Exemplo de paginação:**
```bash
GET /api/users?page=0&size=20&sort=email,asc
```

### 📱 Escaneamento (`/api/scan`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/scan` | Registra novo escaneamento |
| GET | `/api/scan/history?email={email}` | Lista histórico de escaneamentos |
| POST | `/api/scan/favorite/{productId}?email={email}` | Adiciona produto aos favoritos |
| DELETE | `/api/scan/favorite/{productId}?email={email}` | Remove produto dos favoritos |
| GET | `/api/scan/favorites?email={email}` | Lista produtos favoritos |

### 🌱 Impacto Ambiental (`/api/impact`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/impact/{productId}` | Obtém impacto ambiental do produto |
| POST | `/api/impact` | Cria/atualiza dados de impacto |

### 🥗 Nutrição (`/api/nutrition`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/nutrition/{productId}` | Lista informações nutricionais |
| POST | `/api/nutrition` | Adiciona informação nutricional |
| DELETE | `/api/nutrition/{id}` | Remove informação nutricional |

### ❤️ Saúde (`/api/health`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/health` | Verifica status da aplicação |

## 🧪 Testes da API

### Coleção Postman
A aplicação inclui uma coleção completa de testes no Postman disponível em:
- **Arquivo:** `postman/EcoTrack-Oracle.postman_collection.json`
- **Importe no Postman** para testar todos os endpoints

### Exemplos de Requisições

#### Criar Produto
```bash
POST /api/products
Content-Type: application/json

{
  "id": null,
  "name": "Granola Integral",
  "category": "Cereais",
  "kcal100g": 380.0,
  "co2PerUnit": 0.9,
  "barcode": "7891000310101"
}
```

#### Registrar Escaneamento
```bash
POST /api/scan
Content-Type: application/json

{
  "email": "demo@ecotrack.com",
  "barcode": "7891000310101"
}
```

#### Buscar Produto por Código de Barras
```bash
GET /api/products/barcode/7891000310101
```

## 📹 Vídeo de Apresentação

### Link do Vídeo

🔗 **[Acessar Vídeo de Apresentação](https://www.youtube.com/watch?v=SEU_VIDEO_AQUI)**

> ⚠️ **Nota**: A equipe deve adicionar o link do vídeo do YouTube ou plataforma escolhida acima.

### Conteúdo do Vídeo

O vídeo apresenta:
- ✅ **Proposta Tecnológica**: Explicação da solução EcoTrack Oracle API
- ✅ **Público-Alvo**: Consumidores conscientes, empresas de varejo, organizações ambientais
- ✅ **Problemas Resolvidos**: 
  - Rastreamento de impacto ambiental de produtos
  - Transparência nutricional
  - Conscientização do consumidor
  - Dados para tomada de decisão sustentável

## 🛠️ Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **Spring Boot 3.3.3** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring HATEOAS** - Implementação de hypermedia (Nível 3 REST)
- **Oracle Database** - Banco de dados relacional
- **Lombok** - Redução de boilerplate code
- **OpenAPI/Swagger** - Documentação da API
- **Maven** - Gerenciamento de dependências
- **Jakarta Validation** - Validação de dados

## 🔄 Evoluções da Sprint 2

### Implementações Realizadas

1. **HATEOAS Nível 3 (Leonard Richardson)**
   - ✅ Implementação completa de hypermedia como engine da aplicação
   - ✅ Todos os endpoints retornam `EntityModel`, `CollectionModel` e `PagedModel` com links
   - ✅ Links implementados: self, collection, relacionados (impact, nutrition, scan-history, favorites)
   - ✅ Classes `ProductRepresentation` e `UserRepresentation` para encapsular lógica de links

2. **Paginação nas Listagens**
   - ✅ Implementação de `Pageable` e `PagedResourcesAssembler` em todos os endpoints de listagem
   - ✅ Endpoints `/api/products` e `/api/users` agora suportam paginação com parâmetros `page`, `size` e `sort`
   - ✅ Endpoints de busca e categoria também suportam paginação
   - ✅ Respostas paginadas incluem metadados (total de elementos, total de páginas, etc.)

3. **Tratamento Global de Exceções**
   - ✅ Classe `GlobalExceptionHandler` com `@ControllerAdvice` para tratamento centralizado
   - ✅ Mensagens de erro padronizadas com estrutura JSON consistente (`ErrorResponse`)
   - ✅ Tratamento de exceções:
     - `404 NOT_FOUND`: Recursos não encontrados
     - `409 CONFLICT`: Conflitos (email duplicado, código de barras duplicado)
     - `400 BAD_REQUEST`: Erros de validação e tipos inválidos
     - `500 INTERNAL_SERVER_ERROR`: Erros internos do servidor
   - ✅ Validações de entrada com mensagens detalhadas por campo

4. **Validações de Negócio Aprimoradas**
   - ✅ Verificação de email duplicado no cadastro e atualização de usuários
   - ✅ Verificação de código de barras duplicado em produtos
   - ✅ Verificação de favoritos duplicados
   - ✅ Validação de existência de recursos antes de operações de exclusão
   - ✅ Exceções customizadas: `ResourceNotFoundException` e `ResourceConflictException`

5. **Transações e Integridade de Dados**
   - ✅ Uso de `@Transactional` em métodos de criação, atualização e exclusão
   - ✅ Validações de integridade antes de operações no banco de dados
   - ✅ Tratamento de violações de integridade com mensagens adequadas

6. **Refatorações de Código**
   - ✅ Separação de responsabilidades com camada de representação
   - ✅ Controllers seguem padrão RESTful completo com HATEOAS
   - ✅ Uso de `RepresentationModelAssembler` para reutilização
   - ✅ Services refatorados com melhor tratamento de erros e validações
   - ✅ Código mais limpo e manutenível com mensagens de erro descritivas

7. **Testes Automatizados**
   - ✅ Testes unitários para `ProductService` com Mockito
   - ✅ Testes unitários para `UserService` com Mockito
   - ✅ Cobertura de casos de sucesso e falha
   - ✅ Validação de exceções customizadas

8. **Documentação Completa**
   - ✅ Diagrama DER completo
   - ✅ Diagrama de Classes de Entidade
   - ✅ Documentação de arquitetura detalhada
   - ✅ README atualizado com todas as informações obrigatórias

9. **Gestão de Configuração**
   - ✅ Todos os artefatos versionados no GitHub
   - ✅ Coleção Postman completa para testes
   - ✅ Documentação organizada em pasta `documentos/`

### Melhorias de Qualidade

- **Tratamento de Erros Robusto**: Todas as exceções são tratadas de forma consistente
- **Validações Completas**: Validação em múltiplas camadas (entrada, negócio, banco)
- **Mensagens Descritivas**: Erros retornam mensagens claras e acionáveis
- **Código Testável**: Services isolados e testáveis com mocks
- **Performance**: Paginação reduz carga no banco de dados e melhora resposta da API

## 📋 Padrões de Projeto Implementados

- **Repository Pattern** - Abstração do acesso aos dados
- **Service Layer Pattern** - Separação da lógica de negócio
- **DTO Pattern** - Transferência de dados entre camadas
- **Converter Pattern** - Conversão UUID ↔ RAW(16)
- **Builder Pattern** - Construção de objetos complexos (via Lombok)

## 🔧 Configurações Especiais

### Conversão UUID para RAW(16)
A aplicação utiliza um conversor customizado (`UuidRaw16Converter`) para trabalhar com UUIDs no Oracle Database usando o tipo RAW(16), otimizando o armazenamento e performance.

### Validações Implementadas
- **@NotBlank** - Campos obrigatórios
- **@Email** - Validação de formato de email
- **@Valid** - Validação de objetos complexos
- **Validações de Negócio** - Verificação de emails e códigos de barras duplicados
- **Tratamento de Exceções** - Respostas padronizadas com `ErrorResponse`

### Tratamento de Erros

A API retorna respostas de erro padronizadas com a seguinte estrutura:

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "produto não encontrado com id: 123e4567-e89b-12d3-a456-426614174000",
  "path": "/api/products/123e4567-e89b-12d3-a456-426614174000"
}
```

Para erros de validação, a resposta inclui detalhes por campo:

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "erros de validação encontrados",
  "path": "/api/products",
  "validationErrors": [
    {
      "field": "name",
      "message": "não deve estar em branco",
      "rejectedValue": ""
    }
  ]
}
```

## 📝 Documentação Adicional

Toda a documentação do projeto está disponível na raiz e na pasta `documentos/`:

### 📋 Documentos na Raiz do Projeto

### 📁 Documentos na Pasta `documentos/`

- ✅ **[Documentação Completa de Arquitetura](documentos/ARQUITETURA.md)**
  - Diagrama detalhado de arquitetura em camadas
  - Fluxos de dados e requisições
  - Padrões arquiteturais aplicados
  - Tecnologias e frameworks utilizados

- ✅ **[Diagrama Entidade Relacionamento (DER)](documentos/DIAGRAMA_DER.md)**
  - Modelo de dados completo
  - Relacionamentos entre entidades
  - Constraints e regras de integridade
  - Explicação dos relacionamentos

- ✅ **[Diagrama de Classes de Entidade](documentos/DIAGRAMA_CLASSES.md)**
  - Estrutura das classes de domínio
  - Relacionamentos JPA
  - Anotações e mapeamentos
  - Padrões de projeto aplicados

### 🧪 Testes

- ✅ **[Coleção Postman Completa](postman/EcoTrack-Oracle.postman_collection.json)**
  - Todos os endpoints da API organizados por categoria
  - Exemplos de requisições prontas para uso
  - Importar no Postman ou Insomnia para testar

### 📍 Localização dos Arquivos

```
ecotrack-oracle-api-full/
├── README.md                           ← Este arquivo
├── documentos/
│   ├── ARQUITETURA.md                  ← Arquitetura detalhada
│   ├── DIAGRAMA_DER.md                 ← Diagrama DER
│   └── DIAGRAMA_CLASSES.md             ← Diagrama de Classes
└── postman/
    └── EcoTrack-Oracle.postman_collection.json  ← Testes
```

## 📚 Padrão REST e HATEOAS

A aplicação está em conformidade com:

- ✅ **Conceitos fundamentais do REST** (Roy Fielding)
- ✅ **Modelo de maturidade nível 3** (Leonard Richardson - HATEOAS)
  - Recursos incluem links para ações relacionadas
  - Clientes navegam pela API através de links hipermidiáticos
  - Exemplo de resposta com HATEOAS:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Granola Integral",
  "category": "Cereais",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/products/123e4567-e89b-12d3-a456-426614174000"
    },
    "products": {
      "href": "http://localhost:8080/api/products"
    },
    "impact": {
      "href": "http://localhost:8080/api/impact/123e4567-e89b-12d3-a456-426614174000"
    },
    "nutrition": {
      "href": "http://localhost:8080/api/nutrition/123e4567-e89b-12d3-a456-426614174000"
    }
  }
}
```