# EcoTrack Oracle API

## 📋 Sobre o Projeto

O **EcoTrack Oracle API** é uma aplicação Spring Boot que permite rastrear o impacto ambiental e nutricional de produtos através de código de barras. A aplicação conecta-se ao Oracle Database e oferece funcionalidades para usuários registrarem escaneamentos de produtos, gerenciar favoritos e consultar informações sobre sustentabilidade e nutrição.

## 👥 Equipe de Desenvolvimento
Davi Vasconcelos Souza Rm 559906
Gustavo Dantas Oliveira 560685
Paulo Neto Rm 560262



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

### Diagrama de Arquitetura
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
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │     Oracle Database       │
                    │   (RAW(16) UUID Storage)  │
                    └───────────────────────────┘
```

### Camadas da Aplicação
- **Controller Layer:** Endpoints REST para comunicação
- **Service Layer:** Lógica de negócio e validações
- **Repository Layer:** Acesso aos dados com JPA
- **Domain Layer:** Entidades e mapeamentos ORM

## 📊 Modelo de Dados

## 🔗 Endpoints da API

### 📦 Produtos (`/api/products`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/products` | Lista todos os produtos |
| GET | `/api/products/{id}` | Busca produto por ID |
| GET | `/api/products/barcode/{code}` | Busca produto por código de barras |
| GET | `/api/products/category/{name}` | Lista produtos por categoria |
| GET | `/api/products/search?q={term}` | Busca produtos por nome |
| POST | `/api/products` | Cria novo produto |
| PUT | `/api/products/{id}` | Atualiza produto existente |
| DELETE | `/api/products/{id}` | Remove produto |

### 👤 Usuários (`/api/users`)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/users` | Lista todos os usuários |
| GET | `/api/users/{id}` | Busca usuário por ID |
| GET | `/api/users/by-email?email={email}` | Busca usuário por email |
| POST | `/api/users` | Cria novo usuário |
| PUT | `/api/users/{id}` | Atualiza usuário existente |
| DELETE | `/api/users/{id}` | Remove usuário |

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

## 🛠️ Tecnologias Utilizadas

- **Java 17** - Linguagem de programação
- **Spring Boot 3.3.3** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Oracle Database** - Banco de dados relacional
- **Lombok** - Redução de boilerplate code
- **OpenAPI/Swagger** - Documentação da API
- **Maven** - Gerenciamento de dependências
- **Jakarta Validation** - Validação de dados

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
