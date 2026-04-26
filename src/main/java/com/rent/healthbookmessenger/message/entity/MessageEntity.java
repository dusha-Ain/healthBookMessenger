package com.rent.healthbookmessenger.message.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageEntity {

    private Long id;

    private Long idChat;

    private UUID idSender;

    private String content;

    private LocalDateTime sentAt;

    public MessageEntity() {}

    public MessageEntity(Long id,
                         Long idChat,
                         UUID idSender,
                         String content,
                         LocalDateTime sentAt) {
        this.id = id;
        this.idChat = idChat;
        this.idSender = idSender;
        this.content = content;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    public UUID getIdSender() {
        return idSender;
    }

    public void setIdSender(UUID idSender) {
        this.idSender = idSender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
