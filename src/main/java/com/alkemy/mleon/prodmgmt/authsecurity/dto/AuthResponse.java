package com.alkemy.mleon.prodmgmt.authsecurity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/*
DTO para la respuesta de autenticación.
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Excluye campos nulos del JSON

public class AuthResponse {
    private String token;
    private String message; // Nuevo campo para mensajes personalizados
}
