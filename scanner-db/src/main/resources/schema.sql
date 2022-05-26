CREATE TABLE IF NOT EXISTS chain_endpoints
(
    id                SERIAL PRIMARY KEY,
    endpoint_url      VARCHAR(255) NOT NULL,
    api_key           VARCHAR(72)
);

CREATE TABLE IF NOT EXISTS chain_sync_records
(
    id           SERIAL       PRIMARY KEY,
    sync_time    TIMESTAMP    NOT NULL,
    endpoint_url VARCHAR(72)  NOT NULL,
    block_number INTEGER      NOT NULL
);

CREATE TABLE IF NOT EXISTS token_addresses
(
    id         SERIAL      PRIMARY KEY,
    symbol     VARCHAR(72) NOT NULL,
    address    VARCHAR(72) NOT NULL
);

CREATE TABLE IF NOT EXISTS transfers
(
    id                SERIAL       PRIMARY KEY,
    tx_hash           VARCHAR(100) NOT NULL,
    block_number      INTEGER      NOT NULL,
    from_address      VARCHAR(72)  NOT NULL,
    from_memo         VARCHAR(72),
    to_address        VARCHAR(72)  NOT NULL,
    to_memo           VARCHAR(72),
    is_token_transfer BOOLEAN      NOT NULL DEFAULT FALSE,
    amount            DECIMAL      NOT NULL,
    chain             VARCHAR(25)  NOT NULL,
    token_address     VARCHAR(72)  DEFAULT NULL,
);
