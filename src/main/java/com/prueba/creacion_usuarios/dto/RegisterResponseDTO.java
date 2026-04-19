package com.prueba.creacion_usuarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RegisterResponseDTO(
        UUID id,
        String name,
        String email,
        List<PhoneDTO> phones,
        LocalDateTime created,
        LocalDateTime modified,
        @JsonProperty("last_login")
        LocalDateTime lastLogin,
        String token,
        @JsonProperty("isactive")
        Boolean isActive
) {
}
