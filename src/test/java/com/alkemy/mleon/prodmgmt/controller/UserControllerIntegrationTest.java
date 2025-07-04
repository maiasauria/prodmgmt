package com.alkemy.mleon.prodmgmt.controller;

import com.alkemy.mleon.prodmgmt.dto.UserDto;
import com.alkemy.mleon.prodmgmt.enums.Role;
import com.alkemy.mleon.prodmgmt.model.User;
import com.alkemy.mleon.prodmgmt.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    // MockMvc permite realizar peticiones HTTP a los endpoints de la API
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = User.builder()
                .name("Test User")
                .username("a@a.com")
                .password("password123") // Simulando una contraseña encriptada
                .roles(Set.of(Role.ADMIN))
                .build();
        userRepository.save(testUser);
    }



    @Test
    @WithMockUser(username = "a@a.com", roles = {"ADMIN"})
    void getAll_shouldReturnUsersList() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("a@a.com"));
    }

    @Test
    @WithMockUser(username = "a@a.com", roles = {"ADMIN"})
    void create_shouldCreateUser() throws Exception {
        UserDto newUser = UserDto.builder()
                .name("Nuevo Usuario")
                .username("b@b.com")
                .password("pass123")
                .roles(Set.of("USER"))
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("b@b.com"));
    }

    @Test
    @WithMockUser(username = "a@a.com", roles = {"ADMIN"})
    void update_shouldUpdateUser() throws Exception {
        UserDto updatedUser = UserDto.builder()
                .name("Usuario Actualizado")
                .username("testuser@example.com")
                .roles(Set.of("ADMIN"))
                .build();

        mockMvc.perform(put("/api/v1/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Usuario Actualizado"));
    }

    @Test
    @WithMockUser(username = "a@a.com", roles = {"ADMIN"})
    void delete_shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + testUser.getId()))
                .andExpect(status().isNoContent());
    }

}