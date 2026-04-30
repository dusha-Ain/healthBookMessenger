package com.rent.healthbookmessenger.web.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatEvent {

    private String eventType;

    private Long idChat;

    private UUID idActor;

    private Object data;

    private LocalDateTime timestamp;

    public ChatEvent() {}

    public ChatEvent(String eventType,
                     Long idChat,
                     UUID idActor,
                     Object data,
                     LocalDateTime timestamp) {
        this.eventType = eventType;
        this.idChat = idChat;
        this.idActor = idActor;
        this.data = data;
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    public UUID getIdActor() {
        return idActor;
    }

    public void setIdActor(UUID idActor) {
        this.idActor = idActor;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
