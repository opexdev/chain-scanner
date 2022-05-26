CREATE TABLE IF NOT EXISTS chains
(
    name VARCHAR(72) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS chain_endpoints
(
    id                SERIAL PRIMARY KEY,
    chain_name        VARCHAR(72)  NOT NULL REFERENCES chains (name),
    endpoint_url      VARCHAR(255) NOT NULL,
    endpoint_user     VARCHAR(72),
    endpoint_password VARCHAR(72),
    UNIQUE (chain_name, endpoint_url)
);

CREATE TABLE IF NOT EXISTS chain_sync_records
(
    chain        VARCHAR(72) PRIMARY KEY REFERENCES chains (name),
    time         TIMESTAMP   NOT NULL,
    endpoint_url VARCHAR(72) NOT NULL,
    latest_block INTEGER,
    success      BOOLEAN     NOT NULL,
    error        VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS token_addresses
(
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(72) NOT NULL,
    chain_name VARCHAR(72) NOT NULL REFERENCES chains (name)
    address VARCHAR(72) NOT NULL,
    memo VARCHAR(72)
);

CREATE TABLE IF NOT EXISTS deposits
(
    id               SERIAL PRIMARY KEY,
    hash             TEXT UNIQUE NOT NULL,
    wallet_record_id INTEGER,
    chain            VARCHAR(72) NOT NULL REFERENCES chains (name),
    token            BOOLEAN     NOT NULL,
    token_address    VARCHAR(72),
    amount           DECIMAL     NOT NULL,
    depositor        VARCHAR(72) NOT NULL,
    depositor_memo   VARCHAR(72)
);
