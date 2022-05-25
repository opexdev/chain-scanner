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

CREATE TABLE IF NOT EXISTS chain_sync_schedules
(
    chain       VARCHAR(72) PRIMARY KEY REFERENCES chains (name),
    retry_time  TIMESTAMP NOT NULL,
    delay       INTEGER   NOT NULL,
    error_delay INTEGER   NOT NULL
);

CREATE TABLE IF NOT EXISTS chain_sync_retry
(
    id      SERIAL PRIMARY KEY,
    chain   VARCHAR(72) REFERENCES chains (name),
    block   INTEGER NOT NULL,
    retries INTEGER NOT NULL DEFAULT 1,
    synced  BOOLEAN NOT NULL DEFAULT false,
    give_up BOOLEAN NOT NULL DEFAULT false,
    error   TEXT,
    UNIQUE (chain, block)
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
