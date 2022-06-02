CREATE TABLE IF NOT EXISTS chains
(
    name VARCHAR(72) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS chain_sync_schedules
(
    id            SERIAL      PRIMARY KEY,
    chain         VARCHAR(72) NOT NULL UNIQUE REFERENCES chains (name),
    execute_time  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delay         INTEGER     NOT NULL,
    error_delay   INTEGER     NOT NULL,
    enabled       BOOLEAN     NOT NULL DEFAULT TRUE,
    timeout       INTEGER     NOT NULL DEFAULT 30,
    confirmations INTEGER     NOT NULL DEFAULT 0,
    max_retries   INTEGER     NOT NULL DEFAULT 5
);

CREATE TABLE IF NOT EXISTS chain_scanners
(
    id                  SERIAL      PRIMARY KEY,
    chain_name          VARCHAR(72) NOT NULL REFERENCES chains (name),
    url                 VARCHAR(72) NOT NULL,
    max_block_range     INTEGER     NOT NULL DEFAULT 10,
    delay_on_rate_limit INTEGER     NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS chain_sync_retry
(
    id           SERIAL      PRIMARY KEY,
    chain        VARCHAR(72) NOT NULL REFERENCES chains (name),
    block_number INTEGER     NOT NULL,
    retries      INTEGER     NOT NULL DEFAULT 0,
    max_retries  INTEGER     NOT NULL DEFAULT 5,
    synced       BOOLEAN     NOT NULL DEFAULT FALSE,
    give_up      BOOLEAN     NOT NULL DEFAULT FALSE,
    error        TEXT,
    UNIQUE (chain, block_number)
);

CREATE TABLE IF NOT EXISTS chain_sync_records
(
    id           SERIAL      PRIMARY KEY,
    chain        VARCHAR(72) NOT NULL UNIQUE REFERENCES chains (name),
    sync_time    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    block_number INTEGER     NOT NULL
);
