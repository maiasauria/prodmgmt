package com.alkemy.mleon.prodmgmt.service.impl;

import com.alkemy.mleon.prodmgmt.dto.UserDto;
import com.alkemy.mleon.prodmgmt.mapper.UserMapper;
import com.alkemy.mleon.prodmgmt.model.User;
import com.alkemy.mleon.prodmgmt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private static final String USER_ID = "123";

    // Usuario de prueba
    private static final User USER = User.builder()
            .id(USER_ID)
            .name("usuario administrador")
            .username("admin")
            .password("admin123")
            .build();

    // Usuario DTO de prueba
    private static final UserDto USER_DTO = UserDto.builder()
            .id(USER_ID)
            .name("usuario administrador")
            .username("admin")
            .password("admin123")
            .build();

    @Test
    void getAllUsers_shouldReturnListOfUserDTO() {
        // Comportamiento esperado de los mocks
        when(userRepository.findAll()).thenReturn(List.of(USER)); // Simula el retorno de un usuario
        when(userMapper.toDTO(USER)).thenReturn(USER_DTO); // Simula la conversión del usuario a UserDto

        // Act
        List<UserDto> users = userService.getAllUsers();

        // Assert
        assertEquals(1, users.size());
        assertEquals("usuario administrador", users.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void createUser_shouldSaveAndReturnUserDTO() {
        // Arrange
        when(userMapper.toEntity(USER_DTO)).thenReturn(USER);
        when(userRepository.save(USER)).thenReturn(USER);
        when(userMapper.toDTO(USER)).thenReturn(USER_DTO);

        // Act
        UserDto created = userService.createUser(USER_DTO);

        // Assert
        assertNotNull(created);
        assertEquals("admin", created.getUsername());
        verify(userRepository).save(USER);
    }


    @Test
    void updateUser_whenExists_shouldUpdateAndReturnDTO() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));
        when(userRepository.save(USER)).thenReturn(USER);
        when(userMapper.toDTO(USER)).thenReturn(USER_DTO);

        // Act
        UserDto updated = userService.updateUser(USER_ID, USER_DTO);

        // Assert
        assertEquals("usuario administrador", updated.getName());
        verify(userRepository).save(USER);
    }

    @Test
    void updateUser_whenNotExists_shouldThrowException() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUser(USER_ID, USER_DTO));

        assertEquals("User not found with id: 123", ex.getMessage());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void deleteUser_whenExists_shouldDeleteUser() {
        // Arrange
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        // Act
        userService.deleteUser(USER_ID);

        // Assert
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void deleteUser_whenNotExists_shouldThrowException() {
        // Arrange
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(USER_ID));

        assertEquals("User not found with id: 123", ex.getMessage());
        verify(userRepository).existsById(USER_ID);
    }
}