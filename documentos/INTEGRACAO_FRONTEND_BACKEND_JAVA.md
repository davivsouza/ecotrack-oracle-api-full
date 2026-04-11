# Integracao Frontend (App) com Backend Java

## Resumo

Se o app ja estava integrado com o backend Node usando Bearer token, a migracao para o backend Java exige no geral:
- trocar URL base
- manter contrato de rotas/payloads
- garantir envio de `Authorization: Bearer <token>` nas rotas protegidas

## URL base

```env
EXPO_PUBLIC_API_URL=http://<host-da-api-java>:8080
```

## Rotas principais para o app

Publicas:
- `GET /health`
- `POST /auth/register`
- `POST /auth/login`
- `GET /products`
- `GET /products/:id`
- `GET /products/barcode/:barcode`

Protegidas (JWT):
- `GET /auth/me`
- `GET /history`
- `POST /history`
- `PATCH /history/:id`
- `DELETE /history/:id`
- `GET /favorites`
- `POST /favorites`
- `PATCH /favorites/:id`
- `DELETE /favorites/:id`

## Formato de IDs externos

As rotas mobile usam IDs externos gerados por `ExternalIdCodec`:
- Produto: `prod-<uuid_sem_hifen>`
- Usuario: `user-<uuid_sem_hifen>`
- Historico: `history-<uuid_sem_hifen>`
- Favorito: `favorite-<uuid_sem_hifen>`

A API tambem aceita UUID padrao em alguns cenarios internos.

## Payloads de exemplo

## Register

`POST /auth/register`

```json
{
  "name": "Seu Nome",
  "email": "voce@email.com",
  "password": "123456"
}
```

## Login

`POST /auth/login`

```json
{
  "email": "voce@email.com",
  "password": "123456"
}
```

## Criar historico

`POST /history`

```json
{
  "productId": "prod-12345678123412341234123456789abc",
  "note": "opcional"
}
```

## Criar favorito

`POST /favorites`

```json
{
  "productId": "prod-12345678123412341234123456789abc",
  "note": "opcional"
}
```

## Checklist de migracao

- [ ] API Java rodando
- [ ] URL base atualizada no app
- [ ] Login retorna `token` e `user`
- [ ] Header Bearer enviado nas rotas protegidas
- [ ] `/auth/me` funcionando com token
- [ ] Fluxos de `/history` e `/favorites` completos

## Observacoes

- O backend possui duas familias de endpoints: mobile-friendly e legados `/api/**`.
- Para o app React Native, use preferencialmente as rotas sem `/api` listadas acima.
- Em ambiente local com celular fisico, use IP da maquina (nao `localhost`).

