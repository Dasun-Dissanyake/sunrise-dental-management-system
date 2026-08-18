package com.sunrisedental.dto;

import com.sunrisedental.model.Role;
import com.sunrisedental.model.User;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for exposing safe user information without credentials.
 */
public class UserResponse {

    private Long id;
    private String username;
    private String fullName;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String fullName, Role role, boolean enabled, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public static UserResponse fromUser(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
