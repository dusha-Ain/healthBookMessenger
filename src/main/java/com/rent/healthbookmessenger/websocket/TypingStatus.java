package com.rent.healthbookmessenger.websocket;


import java.util.UUID;

/**
 * DTO для статуса печатания
 */
public class TypingStatus {
    private final Long chatId;
    private final UUID userId;
    private final Boolean isTyping;
    private final Long timestamp;

    public TypingStatus(Long chatId, UUID userId, Boolean isTyping, Long timestamp) {
        this.chatId = chatId;
        this.userId = userId;
        this.isTyping = isTyping;
        this.timestamp = timestamp;
    }

    public Long getChatId() {
        return chatId;
    }

    public UUID getUserId() {
        return userId;
    }

    public Boolean getIsTyping() {
        return isTyping;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
