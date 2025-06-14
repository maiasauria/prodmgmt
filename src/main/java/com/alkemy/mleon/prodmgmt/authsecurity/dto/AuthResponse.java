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
public class AuthResponse {
    private String token;
}