package com.marketplace.model;

public class User {

    public enum Role { BUYER, SELLER, ADMIN }

    private final String id;
    private String name;
    private String email;
    private Role role;

    public User(String id, String name, String email, Role role) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("User id must not be blank");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("User name must not be blank");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email address");
        if (role == null) throw new IllegalArgumentException("Role must not be null");
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', email='%s', role=%s}", id, name, email, role);
    }
}
