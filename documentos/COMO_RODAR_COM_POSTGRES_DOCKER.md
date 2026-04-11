# Como rodar com PostgreSQL + Docker

Este guia sobe o banco local e roda a API no profile `postgres`.

## 1) Subir PostgreSQL

Na raiz do projeto:

```bash
docker compose -f docker-compose.postgres.yml up -d
```

Validar container:

```bash
docker ps
```

Esperado:
- container `ecotrack-postgres` em estado `healthy`
- porta `5432` publicada

## 2) Rodar a API com profile postgres

```bash
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
```

Esse profile usa por padrao:
- `DB_URL=jdbc:postgresql://localhost:5432/ecotrack`
- `DB_USER=ecotrack`
- `DB_PASSWORD=ecotrack`

Schema:
- `src/main/resources/schema-postgres.sql` e aplicado automaticamente.

## 3) Teste rapido de endpoints

- Swagger: `http://localhost:8080/swagger-ui.html`
- API docs: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/health`

Observacao importante:
- O health usa query Oracle (`SELECT 1 FROM DUAL`).
- Em Postgres, o campo `database` da resposta pode vir `down` mesmo com API funcionando.

## 4) Rodar testes

```bash
mvn test
```

## 5) Variaveis uteis (opcional)

```bash
export SPRING_PROFILES_ACTIVE=postgres
export DB_URL=jdbc:postgresql://localhost:5432/ecotrack
export DB_USER=ecotrack
export DB_PASSWORD=ecotrack

# seguranca
export JWT_SECRET=ecotrack-dev-secret
export JWT_EXP_MINUTES=10080
export ADMIN_EMAILS=admin@ecotrack.com

# integracoes
export OPEN_FOOD_FACTS_URL=https://world.openfoodfacts.org/api/v2
export INTERNAL_API_BASE_URL=http://localhost:8080

# mensageria (opcional)
export MESSAGING_ENABLED=false
```

## 6) Mensageria (opcional)

Se quiser testar eventos RabbitMQ:
- ative `MESSAGING_ENABLED=true`
- configure exchange/queue/routing key via env vars
- garanta um broker RabbitMQ acessivel

Eventos sao publicados em fluxos de historico e favoritos.

