package com.prueba.creacion_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.creacion_usuarios.dto.PhoneDTO;
import com.prueba.creacion_usuarios.dto.RegisterRequestDTO;
import com.prueba.creacion_usuarios.dto.RegisterResponseDTO;
import com.prueba.creacion_usuarios.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/v1/users - Registro exitoso retorna 201 CREATED y el DTO de respuesta")
    void save_ValidRequest_ShouldReturnCreatedAndResponseDTO() throws Exception {
        // Arrange
        List<PhoneDTO> phones = List.of(new PhoneDTO("1234567", "1", "57"));
        RegisterRequestDTO request = new RegisterRequestDTO("Juan Rodriguez", "juan@dominio.cl", "Password1", phones);

        RegisterResponseDTO expectedResponse = new RegisterResponseDTO(
                UUID.randomUUID(),
                request.name(),
                request.email(),
                phones,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true
        );

        when(userService.save(any(RegisterRequestDTO.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Juan Rodriguez"))
                .andExpect(jsonPath("$.email").value("juan@dominio.cl"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.modified").exists())
                .andExpect(jsonPath("$.last_login").exists())
                .andExpect(jsonPath("$.isactive").value(true))
                .andExpect(jsonPath("$.phones").isArray())
                .andExpect(jsonPath("$.phones[0].number").value("1234567"))
                .andExpect(jsonPath("$.phones[0].citycode").value("1"))
                .andExpect(jsonPath("$.phones[0].contrycode").value("57"));
    }

}