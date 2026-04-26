--liquibase formatted sql

--changeset create:1
CREATE TABLE IF NOT EXISTS chat
(
    id              BIGSERIAL PRIMARY KEY,
    id_profile      UUID      NOT NULL,
    id_consultant   UUID,
    status          VARCHAR(10) CHECK ( status in ('ACTIVE', 'CLOSED', 'WAITING') ),
    created_at      TIMESTAMP NOT NULL,
    last_message_at TIMESTAMP,
    CONSTRAINT unique_chat UNIQUE (id_profile, id_consultant)
);