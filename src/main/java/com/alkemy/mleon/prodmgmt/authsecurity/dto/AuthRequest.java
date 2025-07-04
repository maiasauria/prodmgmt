package com.alkemy.mleon.prodmgmt.authsecurity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * DTO para la solicitud de autenticación.
 * Contiene el nombre de usuario y la contraseña.
 */
@Data
@Builder
@AllArgsConstructor
public class AuthRequest {
    @NotBlank
    @Email
    private String username;

    @NotBlank
    @Size(min = 8, max = 20, message = "Debe tener entre 8 y 20 caracteres")
    private String password;
}
