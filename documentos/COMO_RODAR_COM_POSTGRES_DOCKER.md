# Como Rodar o Projeto com PostgreSQL (Docker)

Este projeto agora suporta rodar localmente com PostgreSQL, sem depender do Oracle da FIAP.

## 1) Subir o banco com Docker

Na raiz do projeto:

```bash
docker compose -f docker-compose.postgres.yml up -d
```

Verifique saúde do container:

```bash
docker ps
```

## 2) Garantir Java 17 no terminal

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.18/libexec/openjdk.jdk/Contents/Home
export PATH=/opt/homebrew/bin:$JAVA_HOME/bin:$PATH
```

## 3) Rodar a API com profile `postgres`

```bash
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
```

Com isso, a API vai usar:

- `jdbc:postgresql://localhost:5432/ecotrack`
- user: `ecotrack`
- password: `ecotrack`

## 4) Testar rápido

- Health: `GET http://localhost:8080/health`
- Swagger: `http://localhost:8080/swagger-ui.html`

## 5) Rodar testes/build

```bash
SPRING_PROFILES_ACTIVE=postgres mvn test
SPRING_PROFILES_ACTIVE=postgres mvn -DskipTests package
```

## Variáveis opcionais

Se quiser sobrescrever credenciais/host:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/ecotrack
export DB_USER=ecotrack
export DB_PASSWORD=ecotrack
export SPRING_PROFILES_ACTIVE=postgres
```

## Observações

- O schema do Postgres é criado automaticamente via `src/main/resources/schema-postgres.sql`.
- O profile Oracle continua existindo no `application.properties` padrão.
- Para integrar o front, em geral basta trocar a URL base da API.
