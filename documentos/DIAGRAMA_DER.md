# Diagrama Entidade Relacionamento (DER) - EcoTrack Oracle API

## Descrição do Modelo de Dados

### Entidades Principais

#### USERS (UserAccount)
- **ID**: RAW(16) UUID - Chave Primária
- **EMAIL**: VARCHAR(200) - Único, Not Null
- **PASSWORD_HASH**: VARCHAR(200) - Not Null
- **DISPLAY_NAME**: VARCHAR(120)
- **CREATED_AT**: TIMESTAMP - Not Null
- **UPDATED_AT**: TIMESTAMP - Not Null

#### PRODUCTS
- **ID**: RAW(16) UUID - Chave Primária
- **NAME**: VARCHAR(200) - Not Null
- **CATEGORY**: VARCHAR(120) - Not Null
- **KCAL_100G**: NUMBER(10,2)
- **CO2_PER_UNIT**: NUMBER(10,3)
- **BARCODE**: VARCHAR(64)

#### SCAN_HISTORY
- **ID**: RAW(16) UUID - Chave Primária
- **USER_ID**: RAW(16) UUID - Foreign Key → USERS(ID)
- **PRODUCT_ID**: RAW(16) UUID - Foreign Key → PRODUCTS(ID)
- **SCANNED_AT**: TIMESTAMP - Not Null
- **SOURCE**: VARCHAR(60) - (MOBILE/WEB)

#### FAVORITES
- **USER_ID**: RAW(16) UUID - Parte da Chave Composta, Foreign Key → USERS(ID)
- **PRODUCT_ID**: RAW(16) UUID - Parte da Chave Composta, Foreign Key → PRODUCTS(ID)
- **CREATED_AT**: TIMESTAMP - Not Null
- **Chave Primária Composta**: (USER_ID, PRODUCT_ID)

#### PRODUCT_IMPACT
- **PRODUCT_ID**: RAW(16) UUID - Chave Primária, Foreign Key → PRODUCTS(ID)
- **CO2_PER_UNIT**: NUMBER(10,3)
- **WATER_L**: NUMBER(10,2)
- **ORIGIN**: VARCHAR(120)
- **UPDATED_AT**: TIMESTAMP - Not Null
- **Relacionamento**: 1:1 com PRODUCTS

#### PRODUCT_NUTRITION
- **ID**: RAW(16) UUID - Chave Primária
- **PRODUCT_ID**: RAW(16) UUID - Foreign Key → PRODUCTS(ID)
- **NUTRI_KEY**: VARCHAR(80) - Not Null
- **NUTRI_VALUE**: VARCHAR(120) - Not Null
- **Relacionamento**: N:1 com PRODUCTS (um produto pode ter várias informações nutricionais)

## Relacionamentos

### USERS ↔ SCAN_HISTORY
- **Tipo**: 1:N (One-to-Many)
- **Cardinalidade**: Um usuário pode ter muitos escaneamentos, um escaneamento pertence a um usuário
- **Constraint**: USER_ID em SCAN_HISTORY referencia USERS(ID)

### PRODUCTS ↔ SCAN_HISTORY
- **Tipo**: 1:N (One-to-Many)
- **Cardinalidade**: Um produto pode ser escaneado várias vezes, um escaneamento referencia um produto
- **Constraint**: PRODUCT_ID em SCAN_HISTORY referencia PRODUCTS(ID)

### USERS ↔ FAVORITES ↔ PRODUCTS
- **Tipo**: N:M (Many-to-Many) através de tabela intermediária
- **Cardinalidade**: Um usuário pode ter vários favoritos, um produto pode ser favorito de vários usuários
- **Constraint**: Chave composta (USER_ID, PRODUCT_ID) com foreign keys para USERS e PRODUCTS

### PRODUCTS ↔ PRODUCT_IMPACT
- **Tipo**: 1:1 (One-to-One)
- **Cardinalidade**: Um produto tem um impacto ambiental, um impacto pertence a um produto
- **Constraint**: PRODUCT_ID em PRODUCT_IMPACT é chave primária e foreign key para PRODUCTS(ID)

### PRODUCTS ↔ PRODUCT_NUTRITION
- **Tipo**: 1:N (One-to-Many)
- **Cardinalidade**: Um produto pode ter várias informações nutricionais, cada informação pertence a um produto
- **Constraint**: PRODUCT_ID em PRODUCT_NUTRITION referencia PRODUCTS(ID)

## Representação Visual (Texto)

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

## Constraints Importantes

1. **UNIQUE Constraints**:
   - USERS.EMAIL deve ser único

2. **FOREIGN KEY Constraints**:
   - SCAN_HISTORY.USER_ID → USERS.ID (ON DELETE CASCADE recomendado)
   - SCAN_HISTORY.PRODUCT_ID → PRODUCTS.ID (ON DELETE RESTRICT recomendado)
   - FAVORITES.USER_ID → USERS.ID (ON DELETE CASCADE)
   - FAVORITES.PRODUCT_ID → PRODUCTS.ID (ON DELETE CASCADE)
   - PRODUCT_IMPACT.PRODUCT_ID → PRODUCTS.ID (ON DELETE CASCADE)
   - PRODUCT_NUTRITION.PRODUCT_ID → PRODUCTS.ID (ON DELETE CASCADE)

3. **CHECK Constraints**:
   - SCAN_HISTORY.SOURCE deve ser 'MOBILE' ou 'WEB' (se aplicável)

4. **NOT NULL Constraints**:
   - Todos os campos marcados como Not Null no modelo acima

## Observações sobre UUID RAW(16)

- Todas as chaves primárias e foreign keys usam UUID armazenado como RAW(16) no Oracle
- Isso otimiza o armazenamento comparado ao VARCHAR(36)
- A conversão é feita através de `UuidRaw16Converter` customizado na aplicação

