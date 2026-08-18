package com.sunrisedental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrisedental.dto.LoginRequest;
import com.sunrisedental.dto.UserResponse;
import com.sunrisedental.model.Role;
import com.sunrisedental.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/v1/auth/login with valid credentials returns 200 OK and ApiResponse")
    void testRestLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken("admin", "admin123");
        UserResponse userResponse = new UserResponse(1L, "admin", "Admin User", Role.ADMIN, true, LocalDateTime.now());

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(userService.findByUsername("admin")).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Authentication successful"))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login with invalid credentials returns 401 Unauthorized")
    void testRestLogin_BadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrongpass");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password."));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/auth/me returns authenticated user details without password")
    void testGetCurrentUser_Success() throws Exception {
        UserResponse mockResponse = new UserResponse(1L, "admin_user", "Admin User", Role.ADMIN, true, LocalDateTime.now());
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("admin_user"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }
}
