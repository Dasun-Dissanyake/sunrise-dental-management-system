package com.sunrisedental.service;

import com.sunrisedental.model.User;
import com.sunrisedental.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Initializer skips user creation if environment variables are not provided")
    void testInit_MissingEnvVars() {
        AdminUserInitializer initializer = new AdminUserInitializer(userRepository, passwordEncoder) {
            @Override
            protected String getEnv(String name) {
                return null;
            }
        };

        initializer.run();

        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Initializer skips saving if user already exists")
    void testInit_UserAlreadyExists() {
        AdminUserInitializer initializer = new AdminUserInitializer(userRepository, passwordEncoder) {
            @Override
            protected String getEnv(String name) {
                if ("SUNRISE_ADMIN_USERNAME".equals(name)) return "admin";
                if ("SUNRISE_ADMIN_PASSWORD".equals(name)) return "admin123";
                return null;
            }
        };

        when(userRepository.existsByUsername("admin")).thenReturn(true);

        initializer.run();

        verify(userRepository).existsByUsername("admin");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Initializer creates admin user when environment variables are provided and user does not exist")
    void testInit_CreateAdminSuccess() {
        AdminUserInitializer initializer = new AdminUserInitializer(userRepository, passwordEncoder) {
            @Override
            protected String getEnv(String name) {
                if ("SUNRISE_ADMIN_USERNAME".equals(name)) return "admin";
                if ("SUNRISE_ADMIN_PASSWORD".equals(name)) return "admin123";
                return null;
            }
        };

        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("$2a$10$encodedpassword");

        initializer.run();

        verify(userRepository).existsByUsername("admin");
        verify(userRepository).save(any(User.class));
    }
}
