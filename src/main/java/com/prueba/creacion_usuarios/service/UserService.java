package com.prueba.creacion_usuarios.service;

import com.prueba.creacion_usuarios.dto.RegisterRequestDTO;
import com.prueba.creacion_usuarios.dto.RegisterResponseDTO;

public interface UserService {
    RegisterResponseDTO save(RegisterRequestDTO requestDTO);
}
