# Integração Frontend (App) com Backend Java

## Resposta curta
Se o front já está integrado com o backend Node usando as mesmas rotas (`/auth`, `/products`, `/history`, `/favorites`) e Bearer token, **não precisa mudar código de integração**.

Na prática, você troca apenas a URL base:

```env
EXPO_PUBLIC_API_URL=http://<host-do-backend-java>:8080
```

## Contrato de API usado pelo app
O backend Java foi alinhado ao contrato do Node para estes endpoints:

- `GET /health`
- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`
- `GET /products`
- `GET /products/:id`
- `GET /products/barcode/:barcode`
- `GET /history`
- `POST /history`
- `PATCH /history/:id`
- `DELETE /history/:id`
- `GET /favorites`
- `POST /favorites`
- `PATCH /favorites/:id`
- `DELETE /favorites/:id`

## O que o front precisa garantir

1. Continuar enviando `Authorization: Bearer <token>` nas rotas protegidas.
2. Manter os mesmos payloads JSON já usados no Node.
3. Trocar somente a URL base da API no `.env`.

## Exemplo de requests (iguais ao Node)

### Register

`POST /auth/register`

```json
{
  "name": "Seu Nome",
  "email": "voce@email.com",
  "password": "123456"
}
```

### Login

`POST /auth/login`

```json
{
  "email": "voce@email.com",
  "password": "123456"
}
```

### Criar histórico (protegido)

`POST /history`

```json
{
  "productId": "prod-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "note": "opcional"
}
```

### Criar favorito (protegido)

`POST /favorites`

```json
{
  "productId": "prod-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "note": "opcional"
}
```

## Checklist rápido de migração

- [ ] Backend Java rodando
- [ ] Banco Oracle acessível
- [ ] `EXPO_PUBLIC_API_URL` apontando para o Java
- [ ] Login retorna `token` e `user`
- [ ] `/auth/me` funciona com token
- [ ] `/history` e `/favorites` funcionando de ponta a ponta

## Observações importantes

- O app **não precisa alterar fluxo de autenticação** se já usa Bearer token.
- Se a URL mudar para HTTPS/proxy/ngrok, ajuste apenas `EXPO_PUBLIC_API_URL`.
- Em ambiente local, confirme CORS/rede entre dispositivo/emulador e API.
