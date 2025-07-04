package com.alkemy.mleon.prodmgmt.repository;

import com.alkemy.mleon.prodmgmt.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


@DataMongoTest
@Testcontainers
class UserRepositoryIntegrationTest {

    // Contenedor de MongoDB para pruebas
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    // Configuración de propiedades dinámicas para la conexión a MongoDB
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = User.builder()
                .name("Usuario")
                .username("a@a.com")
                .password("password")
                .build();

        userRepository.save(testUser);
    }

    // Tests existentes
    @Test
    void findByUsername_shouldReturnUser_whenUsernameExists() {
        Optional<User> foundUser = userRepository.findByUsername("a@a.com");
            assertTrue(foundUser.isPresent());
        assertEquals("a@a.com", foundUser.get().getUsername());
    }

    @Test
    void findByUsername_shouldReturnEmptyOptional_whenUsernameDoesNotExist() {
        Optional<User> foundUser = userRepository.findByUsername("z@z.com");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void existsUserByUsername_shouldReturnTrue_whenUsernameExists() {
        boolean exists = userRepository.existsUserByUsername("a@a.com");
        assertTrue(exists);
    }

    @Test
    void existsUserByUsername_shouldReturnFalse_whenUsernameDoesNotExist() {
        boolean exists = userRepository.existsUserByUsername("z@zz.com");
        assertFalse(exists);
    }

    // Nuevos tests CRUD
    @Test
    void save_shouldCreateNewUser() {
        // Arrange
        User newUser = User.builder()
                .name("Usuario de Prueba")
                .username("b@b.com")
                .password("claevfalsa")
                .build();

        // Act
        User savedUser = userRepository.save(newUser);

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals("b@b.com", savedUser.getUsername());

        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals("Usuario de Prueba", retrievedUser.get().getName());
    }

    @Test
    void findById_shouldReturnUser_whenIdExists() {
        // Act
        Optional<User> foundUser = userRepository.findById(testUser.getId());

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getUsername(), foundUser.get().getUsername());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenIdDoesNotExist() {
        // Act
        Optional<User> foundUser = userRepository.findById("nonexistent-id");

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        // Arrange
        User anotherUser = User.builder()
                .name("Tercer Usuario")
                .username("c@c.com")
                .password("clave123")
                .build();
        userRepository.save(anotherUser);

        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("a@a.com")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("c@c.com")));
    }

    @Test
    void update_shouldModifyExistingUser() {
        // Arrange
        testUser.setName("Juan");
        testUser.setPassword("Clave12");

        // Act
        User updatedUser = userRepository.save(testUser);

        // Assert
        assertEquals(testUser.getId(), updatedUser.getId());
        assertEquals("Juan", updatedUser.getName());
        assertEquals("Clave12", updatedUser.getPassword());

        Optional<User> retrievedUser = userRepository.findById(testUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals("Juan", retrievedUser.get().getName());
    }

    @Test
    void deleteById_shouldRemoveUser() {
        // Act
        userRepository.deleteById(testUser.getId());

        // Assert
        Optional<User> deletedUser = userRepository.findById(testUser.getId());
        assertFalse(deletedUser.isPresent());
        assertEquals(0, userRepository.count());
    }

    @Test
    void count_shouldReturnNumberOfUsers() {
        // Arrange
        User anotherUser = User.builder()
                .name("Pedro")
                .username("d@d.com")
                .password("Clave05")
                .build();
        userRepository.save(anotherUser);

        // Act
        long count = userRepository.count();

        // Assert
        assertEquals(2, count);
    }
}

