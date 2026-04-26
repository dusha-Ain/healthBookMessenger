package com.rent.healthbookmessenger.chat.service;

import com.rent.healthbookmessenger.chat.dto.Chat;
import com.rent.healthbookmessenger.chat.entity.ChatEntity;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    ChatEntity toEntity(Chat chat) {
        return new ChatEntity(
                chat.id(),
                chat.idProfile(),
                chat.idConsultant(),
                chat.status(),
                chat.createdAt(),
                chat.lastMessageAt()
        );
    }

    Chat toDomainChat(ChatEntity chat) {
        return new Chat(
                chat.getId(),
                chat.getIdProfile(),
                chat.getIdConsultant(),
                chat.getStatus(),
                chat.getCreatedAt(),
                chat.getLastMessageAt()
        );
    }
}
