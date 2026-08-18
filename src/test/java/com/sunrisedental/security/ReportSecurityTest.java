package com.sunrisedental.security;

import com.sunrisedental.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private BillService billService;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private PatientService patientService;

    @MockBean
    private DentistService dentistService;

    @MockBean
    private TreatmentService treatmentService;

    @Test
    @DisplayName("Unauthenticated request to /reports should redirect to login")
    void testReports_Unauthenticated() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Unauthenticated request to /api/v1/reports/revenue should redirect to login")
    void testApiReports_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/reports/revenue")
                        .param("startDate", "2026-08-01")
                        .param("endDate", "2026-08-31"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should be allowed to access /reports")
    void testReports_Admin() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    @DisplayName("RECEPTIONIST should be allowed to access /reports")
    void testReports_Receptionist() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DENTIST")
    @DisplayName("DENTIST should be allowed to access /reports")
    void testReports_Dentist() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk());
    }
}