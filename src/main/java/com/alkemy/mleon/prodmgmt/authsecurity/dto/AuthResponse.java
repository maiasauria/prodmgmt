package com.alkemy.mleon.prodmgmt.authsecurity.dto;

import lombok.Builder;
import lombok.Data;

/*
DTO para la respuesta de autenticación.
 */
@Data
@Builder
public class AuthResponse {
    private String token;
    private String message; // Campo adicional para mensajes
}