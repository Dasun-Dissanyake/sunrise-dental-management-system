package com.sunrisedental.service;

import com.sunrisedental.model.Role;
import com.sunrisedental.model.User;
import com.sunrisedental.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("loadUserByUsername returns UserDetails with mapped authority when user exists")
    void testLoadUserByUsername_Success() {
        User user = new User("dentist_john", "$2a$10$hashedpass", "Dr. John", Role.DENTIST, true);
        when(userRepository.findByUsername("dentist_john")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("dentist_john");

        assertNotNull(userDetails);
        assertEquals("dentist_john", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DENTIST")));
    }

    @Test
    @DisplayName("loadUserByUsername throws UsernameNotFoundException when username is invalid")
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("invalid_user")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("invalid_user"));
    }

    @Test
    @DisplayName("loadUserByUsername reflects disabled account status")
    void testLoadUserByUsername_DisabledUser() {
        User user = new User("disabled_user", "$2a$10$hashedpass", "Disabled Staff", Role.RECEPTIONIST, false);
        when(userRepository.findByUsername("disabled_user")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("disabled_user");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
    }
}
