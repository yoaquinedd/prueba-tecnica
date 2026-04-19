package com.prueba.creacion_usuarios.mapper;

import com.prueba.creacion_usuarios.dto.PhoneDTO;
import com.prueba.creacion_usuarios.dto.RegisterRequestDTO;
import com.prueba.creacion_usuarios.dto.RegisterResponseDTO;
import com.prueba.creacion_usuarios.model.Phone;
import com.prueba.creacion_usuarios.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public RegisterResponseDTO toDTO(User userEntity) {
        if (userEntity == null) {
            return null;
        }
        List<PhoneDTO> phonesDTOs = null;
        if(userEntity.getPhones() != null){
            phonesDTOs = userEntity.getPhones().stream().map(
                    phone -> new PhoneDTO(
                            phone.getNumber(),
                            phone.getCityCode(),
                            phone.getCountryCode()
                    )
            ).toList();
        }

        return new RegisterResponseDTO(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                phonesDTOs,
                userEntity.getCreated(),
                userEntity.getModified(),
                userEntity.getLastLogin(),
                userEntity.getToken(),
                userEntity.getActive()
        );
    }

    public User toEntity(RegisterRequestDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setName(userDTO.name());
        user.setEmail(userDTO.email());
        user.setPassword(userDTO.password());

        if (userDTO.phones() != null) {
            List<Phone> phones = userDTO.phones().stream().map(
                    phoneDTO -> {
                        Phone phone = new Phone();
                        phone.setNumber(phoneDTO.number());
                        phone.setCityCode(phoneDTO.cityCode());
                        phone.setCountryCode(phoneDTO.countryCode());
                        phone.setUser(user);
                        return phone;
                    }
            ).toList();
            user.setPhones(phones);
        }

        return user;
    }
}
