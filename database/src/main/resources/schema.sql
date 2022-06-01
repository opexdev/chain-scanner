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
    receiver_address        VARCHAR(72)  NOT NULL,
    receiver_memo           VARCHAR(72),
    is_token_transfer BOOLEAN      NOT NULL DEFAULT FALSE,
    amount            DECIMAL      NOT NULL,
    chain             VARCHAR(25)  NOT NULL,
    token_address     VARCHAR(72)  DEFAULT NULL
);
