package com.alkemy.mleon.prodmgmt.authsecurity.controller;

import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthRequest;
import com.alkemy.mleon.prodmgmt.dto.UserDto;
import com.alkemy.mleon.prodmgmt.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_shouldReturnJwtToken_whenRegistrationDataIsValid() throws Exception {
        // Creamos un DTO de usuario válido
        UserDto validUserRequest = UserDto.builder()
                .name("Test User")
                .username("a@a.com")
                .password("clave123")
                .roles(Set.of("USER"))
                .build();

        // Realizamos la petición POST al endpoint de registro
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void authenticateUser_shouldReturnJwtToken_whenCredentialsAreValid() throws Exception {
        // Creamos un usuario de prueba y lo registramos
        UserDto testUser = UserDto.builder()
                .name("Auth Test User")
                .username("b@b.com")
                .password("testPassword")
                .roles(Set.of("USER"))
                .build();

        // Registramos el usuario de prueba
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)));

        // Creamos una solicitud de autenticación con las credenciales válidas
        AuthRequest validCredentials = AuthRequest.builder()
                .username("b@b.com")
                .password("testPassword")
                .build();

        // Realizamos la petición POST al endpoint de autenticación
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCredentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
    @Test
    void authenticateUser_shouldReturnBadRequestStatus_whenCredentialsAreInvalid() throws Exception {
        // Arrange
        AuthRequest invalidCredentials = AuthRequest.builder()
                .username("z@z.com")
                .password("clave111")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCredentials)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas: z@z.com"));
    }

    @Test
    void registerUser_shouldReturnBadRequest_whenUsernameAlreadyExists() throws Exception {
        // Arrange - First register a user
        UserDto initialUser = UserDto.builder()
                .name("Existing User")
                .username("duplicate@test.com")
                .password("initialPass")
                .roles(Set.of("USER"))
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(initialUser)));

        // Try to register same username again
        UserDto duplicateUser = UserDto.builder()
                .name("Duplicate User")
                .username("duplicate@test.com")  // Same username
                .password("differentPass")
                .roles(Set.of("USER"))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest());
    }
}