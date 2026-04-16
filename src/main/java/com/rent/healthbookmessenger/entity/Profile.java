package com.rent.healthbookmessenger.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record Profile(
        @Null
        @Schema(description = "Идентификатор профиля",
                example = "0ded9ae3-22c3-4ce6-97b0-53fe1d50217d")
        UUID id,

        @Size(min = 2, max = 20)
        @Schema(description = "Имя профиля", example = "Андрей")
        String Name,

        @NotNull
        @Size(max = 25)
        @Schema(description = "Логин профиля", example = "Muzzily")
        String login,

        @Null
        @Schema(description = "Роль профиля", example = "ADMIN")
        String role) {

}
