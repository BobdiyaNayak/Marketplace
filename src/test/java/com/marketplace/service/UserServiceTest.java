package com.marketplace.service;

import com.marketplace.model.User;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new UserRepository());
    }

    @Test
    void registerUser_shouldPersistAndReturnUser() {
        User user = userService.registerUser("Alice", "alice@example.com", User.Role.BUYER);

        assertNotNull(user.getId());
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals(User.Role.BUYER, user.getRole());
    }

    @Test
    void registerUser_shouldThrow_whenEmailAlreadyRegistered() {
        userService.registerUser("Alice", "alice@example.com", User.Role.BUYER);

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("Alice2", "alice@example.com", User.Role.SELLER));
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        User user = userService.registerUser("Bob", "bob@example.com", User.Role.SELLER);

        Optional<User> found = userService.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getName());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<User> found = userService.findById("nonexistent");

        assertTrue(found.isEmpty());
    }

    @Test
    void findByEmail_shouldReturnUser_whenExists() {
        userService.registerUser("Carol", "carol@example.com", User.Role.BUYER);

        Optional<User> found = userService.findByEmail("carol@example.com");

        assertTrue(found.isPresent());
        assertEquals("Carol", found.get().getName());
    }

    @Test
    void listAllUsers_shouldReturnAllUsers() {
        userService.registerUser("U1", "u1@example.com", User.Role.BUYER);
        userService.registerUser("U2", "u2@example.com", User.Role.SELLER);

        List<User> users = userService.listAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void updateUser_shouldModifyNameAndEmail() {
        User user = userService.registerUser("Old Name", "old@example.com", User.Role.BUYER);

        User updated = userService.updateUser(user.getId(), "New Name", "new@example.com");

        assertEquals("New Name", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void updateUser_shouldThrow_whenEmailIsInvalid() {
        User user = userService.registerUser("Test", "test@example.com", User.Role.BUYER);

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(user.getId(), null, "invalidemail"));
    }

    @Test
    void updateUser_shouldThrow_whenUserNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser("bad-id", "Name", "name@example.com"));
    }

    @Test
    void removeUser_shouldReturnTrue_whenExists() {
        User user = userService.registerUser("Dave", "dave@example.com", User.Role.BUYER);

        boolean removed = userService.removeUser(user.getId());

        assertTrue(removed);
        assertTrue(userService.findById(user.getId()).isEmpty());
    }

    @Test
    void removeUser_shouldReturnFalse_whenNotExists() {
        boolean removed = userService.removeUser("nonexistent");

        assertFalse(removed);
    }
}
