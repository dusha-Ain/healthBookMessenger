package com.rent.healthbookmessenger.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record Message(
        @Null
        @Schema(description = "Идентификатор сообщения", example = "23")
        Long id,

        @NotNull
        @Schema(description = "Идентификатор чата общения", example = "48")
        Long idChat,

        @NotNull
        @Schema(description = "Идентификатор профиля-отправителя",
                example = "0ded9ae3-22c3-4ce6-97b0-53fe1d50217d")
        UUID idSender,

        @NotBlank(message = "Сообщение не должно быть пустым")
        @Size(max = 750)
        @Schema(description = "Текст сообщения",
                example = "Привет, сделал тесты, Музамил?")
        String content,

        @Null
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Дата и время отправки сообщения",
                example = "2024-01-15 14:30:00")
        LocalDateTime sentAt
) {
}