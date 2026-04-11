# Diagrama Entidade-Relacionamento (DER) - EcoTrack Oracle API

## Escopo

Este DER representa o modelo relacional atual usado pela API.
As entidades abaixo correspondem diretamente as classes JPA do projeto.

## Entidades e atributos

## USERS
- `ID` (PK)
- `EMAIL` (UNIQUE, NOT NULL)
- `PASSWORD_HASH` (NOT NULL)
- `DISPLAY_NAME`
- `CREATED_AT` (NOT NULL)
- `UPDATED_AT` (NOT NULL)

## PRODUCTS
- `ID` (PK)
- `NAME` (NOT NULL)
- `CATEGORY` (NOT NULL)
- `KCAL_100G`
- `CO2_PER_UNIT`
- `BARCODE`

## SCAN_HISTORY
- `ID` (PK)
- `USER_ID` (FK -> USERS.ID, NOT NULL)
- `PRODUCT_ID` (FK -> PRODUCTS.ID, NOT NULL)
- `SCANNED_AT` (NOT NULL)
- `SOURCE` (VARCHAR(60), opcional)

## FAVORITES
- `USER_ID` (PK/FK -> USERS.ID)
- `PRODUCT_ID` (PK/FK -> PRODUCTS.ID)
- `CREATED_AT` (NOT NULL)
- Chave primaria composta: (`USER_ID`, `PRODUCT_ID`)

## PRODUCT_IMPACT
- `PRODUCT_ID` (PK/FK -> PRODUCTS.ID)
- `CO2_PER_UNIT`
- `WATER_L`
- `ORIGIN`
- `UPDATED_AT` (NOT NULL)

## PRODUCT_NUTRITION
- `ID` (PK)
- `PRODUCT_ID` (FK -> PRODUCTS.ID, NOT NULL)
- `NUTRI_KEY` (NOT NULL)
- `NUTRI_VALUE` (NOT NULL)

## Relacionamentos

- USERS 1:N SCAN_HISTORY
- PRODUCTS 1:N SCAN_HISTORY
- USERS N:M PRODUCTS via FAVORITES
- PRODUCTS 1:1 PRODUCT_IMPACT
- PRODUCTS 1:N PRODUCT_NUTRITION

## DER em texto

```text
USERS (1) --------- (N) SCAN_HISTORY (N) --------- (1) PRODUCTS
  |                                                        |
  |                                                        +---- (1) PRODUCT_IMPACT
  |
  +---- (N) FAVORITES (N) --------------------------------+

PRODUCTS (1) ----------------------------------------- (N) PRODUCT_NUTRITION
```

## Observacoes tecnicas

- O codigo usa UUID nas entidades.
- O projeto inclui `UuidRaw16Converter` auto-aplicado para compatibilidade com Oracle RAW(16).
- No profile local Postgres (`schema-postgres.sql`), os campos sao criados como tipo `UUID`.
- Nao ha CHECK constraint de `SOURCE` no schema Postgres atual; o campo aceita texto ate 60 caracteres.

