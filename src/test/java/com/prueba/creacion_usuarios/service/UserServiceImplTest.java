package com.prueba.creacion_usuarios.service;

import com.prueba.creacion_usuarios.dto.PhoneDTO;
import com.prueba.creacion_usuarios.dto.RegisterRequestDTO;
import com.prueba.creacion_usuarios.dto.RegisterResponseDTO;
import com.prueba.creacion_usuarios.exception.EmailAlreadyExistsException;
import com.prueba.creacion_usuarios.exception.InvalidFormatException;
import com.prueba.creacion_usuarios.mapper.UserMapper;
import com.prueba.creacion_usuarios.model.User;
import com.prueba.creacion_usuarios.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        userService = new UserServiceImpl(
                userRepository,
                userMapper,
                passwordEncoder,
                jwtService,
                "^[a-zA-Z0-9_]+$",
                "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.(cl)$"
        );
    }

    private RegisterRequestDTO buildValidRequest() {
        List<PhoneDTO> phones = List.of(new PhoneDTO("1234567", "1", "57"));
        return new RegisterRequestDTO("Juan Rodriguez", "juan@dominio.cl", "Password1", phones);
    }

    private User buildUserEntity() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Juan Rodriguez");
        user.setEmail("juan@dominio.cl");
        user.setPassword("encoded_password");
        user.setToken(UUID.randomUUID().toString());
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);
        return user;
    }

    private RegisterResponseDTO buildExpectedResponse(User user) {
        return new RegisterResponseDTO(
                user.getId(), user.getName(), user.getEmail(),
                List.of(new PhoneDTO("1234567", "1", "57")),
                user.getCreated(), user.getModified(), user.getLastLogin(),
                user.getToken(), user.getActive()
        );
    }

    @Test
    @DisplayName("save - registro exitoso retorna DTO con datos del usuario creado")
    void save_WithValidData_ShouldReturnResponseDTO() {
        // Arrange
        RegisterRequestDTO request = buildValidRequest();
        User userEntity = buildUserEntity();
        RegisterResponseDTO expectedResponse = buildExpectedResponse(userEntity);

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded_password");
        when(jwtService.generateToken(userEntity.getEmail())).thenReturn("fake_jwt_token");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(expectedResponse);

        // Act
        RegisterResponseDTO result = userService.save(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.email(), result.email());
        assertEquals(expectedResponse.name(), result.name());
        assertNotNull(result.token());

        verify(userRepository).existsByEmail(request.email());
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("save - email duplicado lanza EmailAlreadyExistsException")
    void save_WithDuplicateEmail_ShouldThrowEmailAlreadyExistsException() {
        // Arrange
        RegisterRequestDTO request = buildValidRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.save(request)
        );

        assertEquals("El correo ya registrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("save - email con formato invalido lanza InvalidFormatException")
    void save_WithInvalidEmail_ShouldThrowInvalidFormatException() {
        // Arrange
        List<PhoneDTO> phones = List.of(new PhoneDTO("1234567", "1", "57"));
        RegisterRequestDTO invalidRequest = new  RegisterRequestDTO("Juan Rodriguez", "juan@dominio.com", "Password1", phones);

        // Act & Assert
        InvalidFormatException exception = assertThrows(
                InvalidFormatException.class,
                () -> userService.save(invalidRequest)
        );

        assertEquals("Formato de email no valido", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("save - password con formato invalido lanza InvalidFormatException")
    void save_WithInvalidPassword_ShouldThrowInvalidFormatException() {
        // Arrange
        List<PhoneDTO> phones = List.of(new PhoneDTO("1234567", "1", "57"));
        RegisterRequestDTO invalidRequest = new  RegisterRequestDTO("Juan Rodriguez", "juan@dominio.cl", "Password1!!", phones);

        // Act & Assert
        InvalidFormatException exception = assertThrows(
                InvalidFormatException.class,
                () -> userService.save(invalidRequest)
        );

        assertEquals("Formato de password no valido", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}