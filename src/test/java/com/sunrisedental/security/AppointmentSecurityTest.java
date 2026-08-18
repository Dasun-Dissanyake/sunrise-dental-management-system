package com.sunrisedental.security;

import com.sunrisedental.dto.AppointmentResponse;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.DentistService;
import com.sunrisedental.service.PatientService;
import com.sunrisedental.service.TreatmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security authorization tests for appointment-related endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppointmentSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private DentistService dentistService;

    @MockBean
    private TreatmentService treatmentService;

    @Test
    @DisplayName("Unauthenticated user cannot access /appointments - redirected to login")
    void testUnauthenticatedAccessToAppointments() throws Exception {
        mockMvc.perform(get("/appointments"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("ADMIN can access GET /appointments")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminCanAccessAppointments() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("RECEPTIONIST can access GET /appointments")
    @WithMockUser(username = "recep1", roles = {"RECEPTIONIST"})
    void testReceptionistCanAccessAppointments() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DENTIST can access GET /appointments (read-only)")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testDentistCanViewAppointments() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unauthenticated user cannot access /api/v1/appointments - redirected")
    void testUnauthenticatedAccessToApiAppointments() throws Exception {
        mockMvc.perform(get("/api/v1/appointments"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("DENTIST can access GET /api/v1/appointments")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testDentistCanAccessApiAppointments() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/appointments"))
                .andExpect(status().isOk());
    }
}
