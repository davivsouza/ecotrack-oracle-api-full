# EcoTrack Oracle API

## 📋 Sobre o Projeto

O **EcoTrack Oracle API** é o backend Spring Boot da solução do grupo para monitoramento de impacto ambiental e nutricional de produtos.

Atendendo ao ajuste solicitado, este repositório ficou **focado apenas no backend**, porque o frontend oficial do projeto está em outro repositório/aplicação. Aqui ficam concentrados:

- **API REST** para produtos, usuários, impacto, nutrição, histórico e favoritos;
- **Spring Security** com autenticação e autorização por perfil;
- **Flyway** para versionamento do banco;
- **Dados seed** para demonstração rápida;
- **Suporte a Oracle** e perfil local com **H2** para testes e apresentação.

## 👥 Equipe

| Nome | RM |
|------|----|
| Davi Vasconcelos Souza | 559906 |
| Gustavo Dantas Oliveira | 560685 |
| Paulo Neto | 560262 |

## ✅ Recorte entregue neste backend

### Sprint 3
- Flyway para criação e evolução do banco;
- Spring Security com dois perfis: `ADMIN` e `CONSUMER`;
- Proteção de rotas por perfil;
- Validações básicas nas entidades e endpoints;
- Fluxos completos no backend para:
  1. cadastro/gestão de usuários autenticáveis;
  2. escaneamento + histórico + favoritos.

### Sprint 4
- Backend preparado para integração com frontend separado;
- Documentação de execução, credenciais demo e roteiro oral;
- Estrutura pronta para deploy com profile Oracle;
- Base consistente para demonstrar integração entre segurança, persistência e regras de negócio.

## 🧱 Tecnologias

- Java 17+
- Spring Boot 3.3.3
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security
- Flyway
- H2 (ambiente local)
- Oracle Database (profile `oracle`)
- Maven

## 🚀 Como executar

### Local com H2
```bash
mvn spring-boot:run
```

### Com profile Oracle
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=oracle
```

## 🔐 Usuários de demonstração

### Admin
- `admin@ecotrack.com`
- `admin123`

### Consumer
- `user@ecotrack.com`
- `user123`

## 🔒 Segurança

A autenticação está configurada com **HTTP Basic** e usuários persistidos no banco.

### Permissões
- `/api/products/**` → `ADMIN`
- `/api/users/**` → `ADMIN`
- `/api/impact/**` → `ADMIN`
- `/api/nutrition/**` → `ADMIN`
- `/api/scan/**` → `ADMIN` ou `CONSUMER`

## 🧪 Banco e migrations

As migrations ficam em `src/main/resources/db/migration`:
- `V1__create_schema.sql`
- `V2__seed_demo_data.sql`

## 📘 Endpoints principais

### Produtos
- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/products/barcode/{code}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

### Usuários
- `GET /api/users`
- `GET /api/users/{id}`
- `GET /api/users/by-email?email=...`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

### Escaneamento
- `POST /api/scan`
- `GET /api/scan/history?email=...`
- `POST /api/scan/favorite/{productId}?email=...`
- `DELETE /api/scan/favorite/{productId}?email=...`
- `GET /api/scan/favorites?email=...`

## 📝 Arquivos auxiliares
- `AVALIACAO_ORAL.md`
- `PENDENCIAS_E_PROXIMOS_PASSOS.md`
- `CHECKLIST_SPRINT_3_4.md`
