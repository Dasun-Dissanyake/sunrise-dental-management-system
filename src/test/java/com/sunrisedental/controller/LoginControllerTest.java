package com.sunrisedental.controller;

import com.sunrisedental.dto.UserResponse;
import com.sunrisedental.model.Role;
import com.sunrisedental.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /login returns 200 OK and login view")
    void testGetLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("Unauthenticated user accessing /dashboard is redirected to /login")
    void testUnauthenticatedUserRedirected() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("Authenticated user can access /dashboard")
    void testAuthenticatedUserAccessDashboard() throws Exception {
        UserResponse mockResponse = new UserResponse(1L, "admin_user", "Admin Full Name", Role.ADMIN, true, LocalDateTime.now());
        when(userService.getCurrentAuthenticatedUser()).thenReturn(mockResponse);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("user"));
    }
}
