package com.prueba.creacion_usuarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PhoneDTO(
        String number,
        @JsonProperty("citycode")
        String cityCode,
        @JsonProperty("contrycode")
        String countryCode
) {
}
