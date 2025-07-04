package com.alkemy.mleon.prodmgmt.repository;

import com.alkemy.mleon.prodmgmt.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryUnitTest {

    @Mock
    private UserRepository userRepository;  // Mocking the UserRepository

    private User createTestUser() {
        return  User.builder()
                .id("1")
                .name("Test User")
                .username("a@a.com")
                .password("password")
                .build();
    }

    @Test
    void findByUsername_shouldReturnUserWhenExists() {
        User user = createTestUser();
        when(userRepository.findByUsername("a@a.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findByUsername("a@a.com");

        assertTrue(foundUser.isPresent());
        assertEquals("a@a.com", foundUser.get().getUsername());
        verify(userRepository).findByUsername("a@a.com");
    }

    @Test
    void findByUsername_shouldReturnEmptyWhenNotExists() {
        when(userRepository.findByUsername("z@zz.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByUsername("z@zz.com");

        assertFalse(foundUser.isPresent());

        verify(userRepository).findByUsername("z@zz.com");

    }

    @Test
    void existsUserByUsername_shouldReturnTrueWhenUserExists() {
        when(userRepository.existsUserByUsername("a@a.com")).thenReturn(true);

        boolean exists = userRepository.existsUserByUsername("a@a.com");

        assertTrue(exists);
        verify(userRepository).existsUserByUsername("a@a.com");
    }

    @Test
    void existsUserByUsername_shouldReturnFalseWhenUserDoesNotExist() {
        when(userRepository.existsUserByUsername("z@zz.com")).thenReturn(false);
        boolean exists = userRepository.existsUserByUsername("z@zz.com");
        assertFalse(exists);
        verify(userRepository).existsUserByUsername("z@zz.com");
    }
}