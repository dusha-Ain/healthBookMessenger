package com.rent.healthbookmessenger.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;
import java.util.UUID;

public record Chat(
        @Null
        @Schema(description = "Идентификатор чата общения", example = "23")
        Long id,

        @NotNull
        @Schema(description = "Идентификатор пользователя",
                example = "0ded9ae3-22c3-4ce6-97b0-53fe1d50217d")
        UUID idProfile,

        @NotNull
        @Schema(description = "Идентификатор консультанта",
                example = "151b3cd3-7a37-4e1f-a4c8-528d65b592b5")
        UUID idConsultant,

        String status,

        @Null
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Дата и время создания чата",
                example = "2024-01-15 14:30:00")// WAITING_FOR_CONSULTANT, ACTIVE, CLOSED
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Дата и время создания последнего сообщения",
                example = "2024-01-15 14:30:00")
        LocalDateTime lastMessageAt
) {
}
