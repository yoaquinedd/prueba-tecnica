package com.prueba.creacion_usuarios.controller;

import com.prueba.creacion_usuarios.dto.RegisterRequestDTO;
import com.prueba.creacion_usuarios.dto.RegisterResponseDTO;
import com.prueba.creacion_usuarios.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<RegisterResponseDTO> save(@RequestBody RegisterRequestDTO requestDTO) {
        return new ResponseEntity<>(userService.save(requestDTO), HttpStatus.CREATED);
    }

}
