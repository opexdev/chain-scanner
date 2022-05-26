CREATE TABLE IF NOT EXISTS chain_endpoints
(
    id                SERIAL PRIMARY KEY,
    endpoint_url      VARCHAR(255) NOT NULL UNIQUE,
    endpoint_user     VARCHAR(72),
    endpoint_password VARCHAR(72)
);

CREATE TABLE IF NOT EXISTS chain_sync_records
(
    id           SERIAL       PRIMARY KEY,
    sync_time    TIMESTAMP    NOT NULL,
    endpoint_url VARCHAR(72)  NOT NULL,
    block_number INTEGER      NOT NULL,
    block_hash   VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS token_addresses
(
    id         SERIAL      PRIMARY KEY,
    symbol     VARCHAR(72) NOT NULL,
    address    VARCHAR(72) NOT NULL,
    memo       VARCHAR(72)
);

CREATE TABLE IF NOT EXISTS deposits
(
    id               SERIAL       PRIMARY KEY,
    tx_hash          VARCHAR(100) UNIQUE NOT NULL,
    block_number     INTEGER,
    amount           DECIMAL      NOT NULL,
    token            BOOLEAN      NOT NULL,
    token_address    VARCHAR(72),
    depositor        VARCHAR(72)  NOT NULL,
    depositor_memo   VARCHAR(72)
);
