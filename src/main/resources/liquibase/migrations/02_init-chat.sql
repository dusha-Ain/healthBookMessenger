CREATE TABLE IF NOT EXISTS chat
(
    id              BIGSERIAL PRIMARY KEY,
    id_profile1     UUID      NOT NULL,
    id_profile2     UUID      NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    last_message_at TIMESTAMP NOT NULL,

    CONSTRAINT FK_id_profile1
        FOREIGN KEY (id_profile1)
            REFERENCES profile (id)
            ON DELETE CASCADE,
    CONSTRAINT FK_id_profile2
        FOREIGN KEY (id_profile2)
            REFERENCES profile (id)
            ON DELETE CASCADE,
    CONSTRAINT unique_chat UNIQUE (id_profile1, id_profile2)
);