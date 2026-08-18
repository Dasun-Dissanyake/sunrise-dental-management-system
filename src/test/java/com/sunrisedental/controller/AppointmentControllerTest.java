package com.sunrisedental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sunrisedental.dto.AppointmentCreateRequest;
import com.sunrisedental.dto.AppointmentResponse;
import com.sunrisedental.dto.AppointmentStatusUpdateRequest;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.model.AppointmentStatus;
import com.sunrisedental.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private com.sunrisedental.service.PatientService patientService;

    @MockBean
    private com.sunrisedental.service.DentistService dentistService;

    @MockBean
    private com.sunrisedental.service.TreatmentService treatmentService;

    private ObjectMapper objectMapper;
    private AppointmentResponse sampleResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleResponse = new AppointmentResponse();
        sampleResponse.setId(1L);
        sampleResponse.setAppointmentNumber("APT-000001");
        sampleResponse.setPatientId(1L);
        sampleResponse.setPatientName("Kavindu Perera");
        sampleResponse.setPatientNumber("PAT-000001");
        sampleResponse.setDentistId(1L);
        sampleResponse.setDentistName("Dr. Amara Perera");
        sampleResponse.setTreatmentId(1L);
        sampleResponse.setTreatmentName("Routine Checkup");
        sampleResponse.setTreatmentCost(new BigDecimal("500.00"));
        sampleResponse.setConsultationFee(new BigDecimal("200.00"));
        sampleResponse.setAppointmentDate(LocalDate.now().plusDays(1));
        sampleResponse.setAppointmentTime(LocalTime.of(9, 0));
        sampleResponse.setStatus(AppointmentStatus.SCHEDULED);
        sampleResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/v1/appointments - ADMIN can list appointments")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllAppointments_Admin() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].appointmentNumber").value("APT-000001"));
    }

    @Test
    @DisplayName("GET /api/v1/appointments - DENTIST can view appointments")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testGetAllAppointments_Dentist() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/appointments"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/appointments - unauthenticated redirects to login")
    void testGetAllAppointments_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/appointments"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("POST /api/v1/appointments - ADMIN can create appointment")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateAppointment_Admin() throws Exception {
        when(appointmentService.createAppointment(any())).thenReturn(sampleResponse);

        AppointmentCreateRequest request = new AppointmentCreateRequest(
                1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(9, 0), null);

        mockMvc.perform(post("/api/v1/appointments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.appointmentNumber").value("APT-000001"));
    }

    @Test
    @DisplayName("POST /api/v1/appointments - RECEPTIONIST can create appointment")
    @WithMockUser(username = "recep1", roles = {"RECEPTIONIST"})
    void testCreateAppointment_Receptionist() throws Exception {
        when(appointmentService.createAppointment(any())).thenReturn(sampleResponse);

        AppointmentCreateRequest request = new AppointmentCreateRequest(
                1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(9, 0), null);

        mockMvc.perform(post("/api/v1/appointments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/v1/appointments - DENTIST cannot create appointment (403)")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testCreateAppointment_Dentist_Forbidden() throws Exception {
        AppointmentCreateRequest request = new AppointmentCreateRequest(
                1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(9, 0), null);

        mockMvc.perform(post("/api/v1/appointments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/v1/appointments/{id} - found appointment returns 200")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAppointmentById() throws Exception {
        when(appointmentService.getAppointmentById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/appointments/number/{number} - found by appointment number")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAppointmentByNumber() throws Exception {
        when(appointmentService.getAppointmentByNumber("APT-000001")).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/appointments/number/APT-000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.appointmentNumber").value("APT-000001"));
    }

    @Test
    @DisplayName("GET /api/v1/appointments/search?query= - search returns results")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSearchAppointments() throws Exception {
        when(appointmentService.searchAppointments("Kavindu")).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/appointments/search").param("query", "Kavindu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].patientName").value("Kavindu Perera"));
    }

    @Test
    @DisplayName("PATCH /api/v1/appointments/{id}/status - ADMIN updates status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateStatus_Admin() throws Exception {
        sampleResponse.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentService.updateAppointmentStatus(eq(1L), any())).thenReturn(sampleResponse);

        AppointmentStatusUpdateRequest statusReq = new AppointmentStatusUpdateRequest(AppointmentStatus.COMPLETED, null);

        mockMvc.perform(patch("/api/v1/appointments/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusReq)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/appointments/{id} - ADMIN can cancel appointment")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCancelAppointment_Admin() throws Exception {
        doNothing().when(appointmentService).cancelAppointment(1L);

        mockMvc.perform(delete("/api/v1/appointments/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/appointments/{id} - DENTIST cannot cancel (403)")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testCancelAppointment_Dentist_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/appointments/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/v1/appointments - double booking returns 400")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateAppointment_DoubleBooking_Returns400() throws Exception {
        when(appointmentService.createAppointment(any()))
                .thenThrow(new CustomApiException("Dr. Amara Perera already has an appointment scheduled at this date and time."));

        AppointmentCreateRequest request = new AppointmentCreateRequest(
                1L, 1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(9, 0), null);

        mockMvc.perform(post("/api/v1/appointments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
