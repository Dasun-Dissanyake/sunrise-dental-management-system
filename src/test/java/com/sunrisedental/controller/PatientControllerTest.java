package com.sunrisedental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrisedental.dto.PatientCreateRequest;
import com.sunrisedental.dto.PatientResponse;
import com.sunrisedental.dto.PatientUpdateRequest;
import com.sunrisedental.service.PatientService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/patients with valid payload returns 201 Created")
    void testCreatePatient_Success() throws Exception {
        PatientCreateRequest request = new PatientCreateRequest("Saman Kumara", "Colombo", "0771234567", "saman@example.com", LocalDate.of(1995, 4, 10), "Male");
        PatientResponse response = new PatientResponse(1L, "PAT-000001", "Saman Kumara", "Colombo", "0771234567", "saman@example.com", LocalDate.of(1995, 4, 10), "Male", LocalDateTime.now(), true);

        when(patientService.registerPatient(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.patientNumber").value("PAT-000001"));
    }

    @Test
    @WithMockUser(username = "receptionist_user", roles = {"RECEPTIONIST"})
    @DisplayName("POST /api/v1/patients with invalid payload returns 400 Bad Request")
    void testCreatePatient_InvalidData() throws Exception {
        PatientCreateRequest invalidRequest = new PatientCreateRequest("", "", "invalid_phone", "not_an_email", null, null);

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @WithMockUser(username = "dentist_user", roles = {"DENTIST"})
    @DisplayName("GET /api/v1/patients returns list of patients")
    void testGetAllPatients() throws Exception {
        PatientResponse response = new PatientResponse(1L, "PAT-000001", "Saman Kumara", "Colombo", "0771234567", null, null, null, LocalDateTime.now(), true);
        when(patientService.getAllPatients()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/v1/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].patientNumber").value("PAT-000001"));
    }

    @Test
    @WithMockUser(username = "dentist_user", roles = {"DENTIST"})
    @DisplayName("GET /api/v1/patients/{id} returns patient profile")
    void testGetPatientById() throws Exception {
        PatientResponse response = new PatientResponse(1L, "PAT-000001", "Saman Kumara", "Colombo", "0771234567", null, null, null, LocalDateTime.now(), true);
        when(patientService.getPatientById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("PUT /api/v1/patients/{id} updates patient profile")
    void testUpdatePatient() throws Exception {
        PatientUpdateRequest updateRequest = new PatientUpdateRequest("Updated Name", "Updated Address", "0779998887", null, null, null, true);
        PatientResponse response = new PatientResponse(1L, "PAT-000001", "Updated Name", "Updated Address", "0779998887", null, null, null, LocalDateTime.now(), true);

        when(patientService.updatePatient(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("Updated Name"));
    }

    @Test
    @WithMockUser(username = "receptionist_user", roles = {"RECEPTIONIST"})
    @DisplayName("DELETE /api/v1/patients/{id} deactivates patient")
    void testDeactivatePatient() throws Exception {
        PatientResponse response = new PatientResponse(1L, "PAT-000001", "Saman", "Colombo", "0771234567", null, null, null, LocalDateTime.now(), false);
        when(patientService.deactivatePatient(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(false));
    }
}
