package com.alkemy.mleon.prodmgmt.authsecurity.dto;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

/*
DTO para la respuesta de autenticación.
 */
@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String message; // Campo adicional para mensajes
}