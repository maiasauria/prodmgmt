package com.alkemy.mleon.prodmgmt.authsecurity.controller;

import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthRequest;
import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthResponse;
import com.alkemy.mleon.prodmgmt.authsecurity.service.AuthService;
import com.alkemy.mleon.prodmgmt.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserDTO request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(AuthResponse.builder()
                    .token(null)
                    .message("Datos inválidos: " + e.getMessage())
                    .build());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(400).body(AuthResponse.builder()
                    .token(null)
                    .message("Error de credenciales: " + e.getMessage())
                    .build());
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(AuthResponse.builder()
                    .token(null)
                    .message("Error interno del servidor: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.authenticate(request));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(AuthResponse.builder()
                    .token(null)
                    .message("Usuario no encontrado: " + e.getMessage())
                    .build());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(AuthResponse.builder()
                    .token(null)
                    .message("Credenciales incorrectas: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(AuthResponse.builder()
                    .token(null)
                    .message("Error interno del servidor: " + e.getMessage())
                    .build());
        }
    }
}