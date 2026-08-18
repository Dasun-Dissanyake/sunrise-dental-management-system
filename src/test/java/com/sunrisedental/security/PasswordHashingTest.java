package com.sunrisedental.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashingTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("Password is stored as a BCrypt hash and never plain text")
    void testPasswordHashing_NotPlainText() {
        String plainPassword = "SecretClinicPassword123!";
        String encodedPassword = passwordEncoder.encode(plainPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(plainPassword, encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"));
    }

    @Test
    @DisplayName("BCrypt matches correct raw password and rejects invalid password")
    void testPasswordMatching() {
        String rawPassword = "ValidPassword2026";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("WrongPassword", encodedPassword));
    }
}
