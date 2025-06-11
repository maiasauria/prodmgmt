package com.alkemy.mleon.prodmgmt.controller;

import com.alkemy.mleon.prodmgmt.dto.UserDTO;
import com.alkemy.mleon.prodmgmt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> lista = userService.getAllUsers();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(UserDTO user) {
        UserDTO newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    //TODO revisar
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody UserDTO user) {
        UserDTO updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    //TODO revisar
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
