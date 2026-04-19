package com.prueba.creacion_usuarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Creacion de usuarios")
                        .version("1.0")
                        .description("API RESTful para la creacion de usuarios")
                        .contact(new Contact()
                                .name("Joaquin Poblete")
                                .email("joaquin.poblete@sermaluc.cl")
                        )
                );
    }
}
