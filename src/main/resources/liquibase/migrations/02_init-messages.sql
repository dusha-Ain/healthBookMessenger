--liquibase formatted sql

--changeset create:1
CREATE TABLE IF NOT EXISTS message
(
    id        BIGSERIAL PRIMARY KEY,
    id_chat   BIGINT    NOT NULL,
    id_sender UUID      NOT NULL,
    content   TEXT      NOT NULL,
    sent_at   TIMESTAMP NOT NULL,
    CONSTRAINT FK_id_chat_chat
        FOREIGN KEY (id_chat)
            REFERENCES chat (id)
            ON DELETE CASCADE
);
