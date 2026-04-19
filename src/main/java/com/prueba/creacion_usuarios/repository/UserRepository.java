package com.prueba.creacion_usuarios.repository;

import com.prueba.creacion_usuarios.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
}
