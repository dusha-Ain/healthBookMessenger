package com.rent.healthbookmessenger.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;


public record ErrorResponseDto(
        String message,

        String detailedMessage,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Moscow")
        LocalDateTime errorTime
) {
}
