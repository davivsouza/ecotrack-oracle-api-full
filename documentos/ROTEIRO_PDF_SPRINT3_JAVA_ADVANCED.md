# Roteiro do PDF - Sprint 3 (Java Advanced)

Este roteiro foi montado para te ajudar a produzir o PDF da Sprint 3 com evidências objetivas dos requisitos do enunciado (págs. 23 a 25 do arquivo da disciplina).

## 1. Capa

Inclua:
- Nome do projeto: EcoTrack Oracle API
- Disciplina: Java Advanced
- Sprint: 3
- Integrantes + RM
    Davi Vasconcelos Souza Rm 559906
    Gustavo Dantas Oliveira 560685
    Paulo Neto Rm 560262
- Data da entrega
- Link do repositório GitHub
    https://github.com/davivsouza/ecotrack-oracle-api-full

## 2. Resumo Executivo (1 página)

Explique em 1 parágrafo:
- Qual problema o sistema resolve
- Quais funcionalidades principais estão entregues na Sprint 3
- Quais tecnologias centrais foram usadas para cumprir os requisitos (Feign, RabbitMQ, Spring Security)

## 3. Mapeamento Requisito x Evidência (obrigatório)

Use uma tabela como esta:

| Requisito do enunciado | Evidência no código | Evidência de execução |
|---|---|---|
| Clientes Feign (síncrono) | `InternalProductClient`, `OpenFoodFactsClient`, `@EnableFeignClients` | Print de chamada funcionando + retorno no Swagger/Postman |
| Mensageria assíncrona | `RabbitMessagingConfig`, `UserActivityEventPublisher`, `UserActivityEventListener` | Print do log consumindo evento |
| Spring Security com 2 perfis | `RoleType` (`USER`, `ADMIN`) e `SecurityConfig` | Print de rota bloqueada/liberada por perfil |
| 2 fluxos completos (não só CRUD) | Fluxo Auth (`register/login/me`) + Fluxo produto por barcode (importação e cálculo) | Sequência de requests e respostas |
| Validações em criação/atualização | DTOs com `@Valid`, `@NotBlank`, `@Email`, `@Size` + validações de negócio em services | Print de erro 400/409 com mensagem |

## 4. Requisito 1 - Feign + Mensageria (50 pontos)

### 4.1 Feign (comprovação técnica)

Mostre prints/snippets de:
- `pom.xml` com `spring-cloud-starter-openfeign`
- `EcoTrackOracleApiApplication` com `@EnableFeignClients`
- `InternalProductClient` e `OpenFoodFactsClient`
- Uso real do Feign nos serviços (`MobileHistoryService` e `MobileFavoriteService`)

### 4.2 Mensageria assíncrona (comprovação técnica)

Mostre prints/snippets de:
- `pom.xml` com `spring-boot-starter-amqp`
- `RabbitMessagingConfig` (exchange, queue, binding)
- `UserActivityEventPublisher` (publicação)
- `UserActivityEventListener` (consumo)
- `application.properties` com `ecotrack.messaging.*`

### 4.3 Evidência de execução (importante)

No PDF, inclua sequência:
1. Subir RabbitMQ (ex.: Docker)
2. Iniciar API com `MESSAGING_ENABLED=true`
3. Executar ação que publica evento (`POST /history` ou `POST /favorites`)
4. Mostrar log do listener processando evento

## 5. Requisito 2 - Spring Security (30 pontos)

### 5.1 Dois tipos de usuário

Comprovar:
- Enum de roles com `USER` e `ADMIN`
- Estratégia para definir admin (lista `ADMIN_EMAILS`)

### 5.2 Proteção de rotas por perfil

Comprovar no PDF:
- Rotas públicas (`/auth/login`, `/auth/register`, docs)
- Rotas autenticadas (`/history`, `/favorites`)
- Rotas de admin (`/api/users/**`, alterações em `/api/products/**`)

### 5.3 Evidência prática (prints)

Monte 3 prints:
1. Sem token: acesso negado (401)
2. Com token USER: acessa `/history`, mas não acessa rota admin
3. Com token ADMIN: acessa rota admin

## 6. Requisito 3 - Funcionalidades completas (20 pontos)

O enunciado pede 2 fluxos completos, não apenas CRUD básico.

Sugestão de fluxos para demonstrar:
- Fluxo A: autenticação completa (`register -> login -> me`)
- Fluxo B: busca/importação por código de barras (`/products/barcode/{barcode}`), com fallback e enriquecimento de dados

Opcional adicional forte:
- Fluxo C: histórico/favoritos com validação por Feign + publicação assíncrona de evento

## 7. Validações (criação e atualização)

No PDF, mostre exemplos de:
- Validação de formato de email/senha no auth
- Validação de campos obrigatórios (`@NotBlank`)
- Conflito de recurso (ex.: email duplicado, favorito duplicado) retornando 409

Inclua prints de respostas de erro padronizadas (400/409/404).

## 8. Qualidade de Código e Penalidades (check preventivo)

Inclua uma seção curta de autoverificação:
- SOLID/DRY: classes de serviço e separação por camadas
- Legibilidade: nomes claros e responsabilidades separadas
- Funcionalidades sem erro evidente (anexar resultado de testes)
- README com instruções completas
- Vídeo de até 5 minutos com demonstração real

## 9. Evidências de execução (anexos recomendados)

Anexe no PDF:
- Print do `mvn test` passando
- Prints dos endpoints no Swagger/Postman
- Prints de logs da mensageria
- Print da estrutura do projeto (`src/main/java/...`)

## 10. Seção final de links

Feche o PDF com:
- Link do repositório
- Link do vídeo no YouTube (obrigatório, sem placeholder)
- Link da coleção Postman (se aplicável)

## 11. Checklist final antes de exportar o PDF

- [ ] Todos os 3 blocos do enunciado (Feign+Mensageria, Security, Fluxos completos) têm evidência
- [ ] Existe evidência de execução real (não só print de código)
- [ ] README está coerente com o que aparece no PDF
- [ ] Link do vídeo é real e funcional
- [ ] PDF está com capa, sumário e conclusão

---

## Sugestão de ordem dos prints no PDF

1. `pom.xml` (dependências chave)
2. classes de Feign
3. classes de mensageria
4. classes/config de security
5. fluxo auth no Postman/Swagger
6. fluxo barcode/importação
7. fluxo history/favorites + log assíncrono
8. testes passando
9. conclusão

Esse roteiro já está alinhado ao que o professor normalmente procura na correção: requisito + prova de implementação + prova de funcionamento.
