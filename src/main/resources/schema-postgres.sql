CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY,
  email VARCHAR(200) NOT NULL UNIQUE,
  password_hash VARCHAR(200) NOT NULL,
  display_name VARCHAR(120),
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
  id UUID PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  category VARCHAR(120) NOT NULL,
  kcal_100g NUMERIC(10, 2),
  co2_per_unit NUMERIC(10, 3),
  barcode VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS product_impact (
  product_id UUID PRIMARY KEY,
  co2_per_unit NUMERIC(10, 3),
  water_l NUMERIC(10, 2),
  origin VARCHAR(120),
  updated_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT fk_product_impact_product
    FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE IF NOT EXISTS product_nutrition (
  id UUID PRIMARY KEY,
  product_id UUID NOT NULL,
  nutri_key VARCHAR(80) NOT NULL,
  nutri_value VARCHAR(120) NOT NULL,
  CONSTRAINT fk_product_nutrition_product
    FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE IF NOT EXISTS scan_history (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  product_id UUID NOT NULL,
  scanned_at TIMESTAMPTZ NOT NULL,
  source VARCHAR(60),
  CONSTRAINT fk_scan_history_user
    FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_scan_history_product
    FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE IF NOT EXISTS favorites (
  user_id UUID NOT NULL,
  product_id UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  CONSTRAINT pk_favorites PRIMARY KEY (user_id, product_id),
  CONSTRAINT fk_favorites_user
    FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_favorites_product
    FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE INDEX IF NOT EXISTS idx_products_barcode ON products (barcode);
CREATE INDEX IF NOT EXISTS idx_scan_history_user_scanned_at ON scan_history (user_id, scanned_at DESC);
