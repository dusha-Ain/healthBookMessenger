CREATE TABLE IF NOT EXISTS message
(
    id        BIGSERIAL PRIMARY KEY,
    id_chat   BIGINT    NOT NULL,
    id_sender UUID      NOT NULL,
    content   TEXT      NOT NULL,
    sent_at   TIMESTAMP NOT NULL,
    edited    BOOLEAN DEFAULT FALSE,
    deleted   BOOLEAN DEFAULT FALSE,

    CONSTRAINT FK_id_chat_chat
        FOREIGN KEY (id_chat)
            REFERENCES chat (id)
            ON DELETE CASCADE,
    CONSTRAINT FK_id_sender_profile
        FOREIGN KEY (id_sender)
            REFERENCES profile (id)
            ON DELETE CASCADE
);
