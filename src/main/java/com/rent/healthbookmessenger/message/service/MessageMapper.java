package com.rent.healthbookmessenger.message.service;

import com.rent.healthbookmessenger.message.dto.Message;
import com.rent.healthbookmessenger.message.entity.MessageEntity;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public Message toDomainMessage(MessageEntity message) {
        return new Message(
                message.getId(),
                message.getIdChat(),
                message.getIdSender(),
                message.getContent(),
                message.getSentAt()
                );
    }

}
