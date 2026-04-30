package com.rent.healthbookmessenger.chat.service;

import com.rent.healthbookmessenger.chat.entity.ChatEntity;
import com.rent.healthbookmessenger.web.exception.EntityToManyResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChatValidator {
    private ChatValidator() {}

    public static Optional<ChatEntity> validateSearchResult(
            List<ChatEntity> chat, Long id){
        if (chat.size() > 1) {
            throw new EntityToManyResultException(
                    String.format("More than one chat found by id = %d", id));
        }
        if (chat.isEmpty()) {
            return Optional.empty();
        }
        return chat.stream().findFirst();
    }

    public static Optional<ChatEntity> validateSearchResultByProfile(
            List<ChatEntity> chat, UUID id){
        if (chat.size() > 1) {
            throw new EntityToManyResultException(
                    String.format("More than one chat found by profile id = %s", id));
        }
        if (chat.isEmpty()) {
            return Optional.empty();
        }
        return chat.stream().findFirst();
    }
}
