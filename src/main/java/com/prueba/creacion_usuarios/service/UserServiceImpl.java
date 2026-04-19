package com.prueba.creacion_usuarios.service;

import com.prueba.creacion_usuarios.dto.RegisterRequestDTO;
import com.prueba.creacion_usuarios.dto.RegisterResponseDTO;
import com.prueba.creacion_usuarios.exception.EmailAlreadyExistsException;
import com.prueba.creacion_usuarios.exception.InvalidFormatException;
import com.prueba.creacion_usuarios.mapper.UserMapper;
import com.prueba.creacion_usuarios.model.User;
import com.prueba.creacion_usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.validation.password.regex}")
    private String passwordRegex;

    @Value("${app.validation.email.regex}")
    private String emailRegex;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RegisterResponseDTO save(RegisterRequestDTO requestDTO) {

        if (!requestDTO.email().matches(emailRegex)) {
            throw new InvalidFormatException("Formato de email no valido");
        }

        if (userRepository.existsByEmail(requestDTO.email())) {
            throw new EmailAlreadyExistsException("El correo ya registrado");
        }

        if (!requestDTO.password().matches(passwordRegex)) {
            throw new InvalidFormatException("Formato de password no valido");
        }

        User user = userMapper.toEntity(requestDTO);
        user.setPassword(passwordEncoder.encode(requestDTO.password()));
        user.setToken(UUID.randomUUID().toString());

        return userMapper.toDTO(userRepository.save(user));
    }
}

