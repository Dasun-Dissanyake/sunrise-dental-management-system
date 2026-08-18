package com.sunrisedental.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrisedental.dto.PatientCreateRequest;
import com.sunrisedental.dto.PatientResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    @Test
    @DisplayName("Unauthenticated user cannot access patient API")
    void testUnauthenticatedUserCannotAccessPatientApi() throws Exception {
        mockMvc.perform(get("/api/v1/patients"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "receptionist", roles = {"RECEPTIONIST"})
    @DisplayName("Receptionist can create patient")
    void testReceptionistCanCreatePatient() throws Exception {
        PatientCreateRequest request = new PatientCreateRequest("Kavindu", "Kandy", "0771234567", null, LocalDate.of(1990, 1, 1), "Male");
        PatientResponse response = new PatientResponse(1L, "PAT-000001", "Kavindu", "Kandy", "0771234567", null, LocalDate.of(1990, 1, 1), "Male", LocalDateTime.now(), true);

        when(patientService.registerPatient(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "dentist", roles = {"DENTIST"})
    @DisplayName("Dentist can view patients")
    void testDentistCanViewPatients() throws Exception {
        mockMvc.perform(get("/api/v1/patients"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "dentist", roles = {"DENTIST"})
    @DisplayName("Dentist cannot create, update, or deactivate patients")
    void testDentistCannotModifyPatients() throws Exception {
        PatientCreateRequest request = new PatientCreateRequest("Kavindu", "Kandy", "0771234567", null, null, null);

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/patients/1"))
                .andExpect(status().isForbidden());
    }
}
