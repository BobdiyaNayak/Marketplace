package com.marketplace.service;

import com.marketplace.model.User;
import com.marketplace.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String name, String email, User.Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        String id = UUID.randomUUID().toString();
        User user = new User(id, name, email, role);
        userRepository.save(user);
        return user;
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(String id, String name, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        if (name != null && !name.isBlank()) user.setName(name);
        if (email != null) {
            if (!email.contains("@")) throw new IllegalArgumentException("Invalid email address");
            user.setEmail(email);
        }
        userRepository.save(user);
        return user;
    }

    public boolean removeUser(String id) {
        return userRepository.delete(id);
    }
}
