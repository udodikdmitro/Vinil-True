CREATE TABLE tokens(
    id            VARCHAR(255) PRIMARY KEY,
    person_id     BIGINT,
    refresh_token VARCHAR(255),
    expires_at    TIMESTAMP,
    created_at    TIMESTAMP DEFAULT now(),
    CONSTRAINT fk_user FOREIGN KEY (person_id) REFERENCES users (id)
);
