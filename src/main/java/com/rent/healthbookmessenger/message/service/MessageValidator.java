package com.rent.healthbookmessenger.message.service;


import com.rent.healthbookmessenger.message.entity.MessageEntity;
import com.rent.healthbookmessenger.web.exception.EntityToManyResultException;

import java.util.List;
import java.util.Optional;

public class MessageValidator {

    private MessageValidator() {}

    public static Optional<MessageEntity> validateSearchResult(
            List<MessageEntity> message, Long id){
        if (message.size() > 1) {
            throw new EntityToManyResultException(
                    String.format("More than one message found by id = %d", id));
        }
        if (message.isEmpty()) {
            return Optional.empty();
        }
        return message.stream().findFirst();
    }
}
