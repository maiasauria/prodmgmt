package com.alkemy.mleon.prodmgmt.controller;

import com.alkemy.mleon.prodmgmt.dto.UserDto;
import com.alkemy.mleon.prodmgmt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios internas del sistema")

public class UserController {

    private final UserService userService;

    @GetMapping()
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Devuelve una lista con todas los usuarios del sistema"
    )
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> lista = userService.getAllUsers();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @ApiResponse(responseCode = "409", description = "El usuario ya existe")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) {
        try {
            UserDto creado = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description
                    = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404",
                    description = "Usuario no encontrado")
    })
    public ResponseEntity<UserDto> actualizar(
            @PathVariable String id,
            @RequestBody UserDto userDTO) {
        UserDto actualizado = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}