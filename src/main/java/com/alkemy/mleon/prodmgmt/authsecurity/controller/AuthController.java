package com.alkemy.mleon.prodmgmt.authsecurity.controller;

import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthRequest;
import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthResponse;
import com.alkemy.mleon.prodmgmt.authsecurity.service.AuthService;
import com.alkemy.mleon.prodmgmt.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserDTO request) {
        log.info("AuthController:register | Intentando registrar nuevo usuario: {}", request.getUsername());
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(201).body(response); // Código 201: Creado
        } catch (IllegalArgumentException e) {
            log.warn("AuthController:register | Usuario ya existe: {}", request.getUsername());
            return ResponseEntity.status(409).body(new AuthResponse(null, "El nombre de usuario ya existe")); // Código 409: Conflicto
        } catch
        (Exception e) {
            log.error("AuthController:register | Error al registrar usuario: {}", e.getMessage());
            return ResponseEntity.status(400).body(new AuthResponse(null, "Error al registrar usuario")); // Código 400: Solicitud incorrecta
        }
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("AuthController:login | Intentando autenticar usuario: {}", request.getUsername());
        try {
            AuthResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response); // Código 200: OK
        } catch (BadCredentialsException e) {
            log.warn("AuthController:login | Credenciales inválidas para usuario: {}", request.getUsername());
            return ResponseEntity.status(401).body(new AuthResponse(null, "Credenciales inválidas")); // Código 401: No autorizado
        } catch (Exception e) {
            log.error("AuthController:login | Error inesperado: {}", e.getMessage());
            return ResponseEntity.status(500).body(new AuthResponse(null, "Error interno del servidor")); // Código 500: Error interno
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("AuthController:handleValidationException | Error de validación: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse(null, errorMessage));
    }
}