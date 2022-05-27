CREATE TABLE IF NOT EXISTS chain_endpoints
(
    id              SERIAL PRIMARY KEY,
    endpoint_url    VARCHAR(255) NOT NULL,
    api_key         VARCHAR(72),
    request_per_sec INTEGER     NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS watch_list
(
    id      SERIAL      PRIMARY KEY,
    address VARCHAR(72) NOT NULL UNIQUE
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
    token_address     VARCHAR(72)  DEFAULT NULL
);
