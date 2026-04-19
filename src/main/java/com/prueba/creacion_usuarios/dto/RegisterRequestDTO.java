package com.prueba.creacion_usuarios.dto;

import java.util.List;

public record RegisterRequestDTO(
                String name,
                String email,
                String password,
                List<PhoneDTO> phones) {
}
