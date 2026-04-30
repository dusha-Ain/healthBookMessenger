package com.rent.healthbookmessenger.message.dto;

import java.util.UUID;

public record IdMessage(
        Long idChat,
        Long idMessage,
        UUID idProfile
) {
}
