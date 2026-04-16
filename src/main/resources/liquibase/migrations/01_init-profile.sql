CREATE TABLE IF NOT EXISTS profile
(
    id      UUID PRIMARY KEY,
    name    VARCHAR(20),
    login   VARCHAR(25) NOT NULL,
    role    VARCHAR(20) CHECK ( role in ('USER', 'ADMIN') )
);