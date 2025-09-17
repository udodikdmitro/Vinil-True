
CREATE TABLE shipping_providers(
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(255),
    code        VARCHAR(255),
    logo_url    VARCHAR(255),
    website_url VARCHAR(255),
    active      BOOLEAN,
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now()
);
