package com.sunrisedental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sunrisedental.dto.BillResponse;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    private BillResponse sampleBillResponse;

    @BeforeEach
    void setUp() {
        sampleBillResponse = new BillResponse();
        sampleBillResponse.setId(1L);
        sampleBillResponse.setBillNumber("REC-000001");
        sampleBillResponse.setAppointmentId(1L);
        sampleBillResponse.setAppointmentNumber("APT-000001");
        sampleBillResponse.setAppointmentDate(LocalDate.now().plusDays(1));
        sampleBillResponse.setAppointmentTime(LocalTime.of(9, 0));
        sampleBillResponse.setPatientNumber("PAT-000001");
        sampleBillResponse.setPatientName("Kavindu Perera");
        sampleBillResponse.setDentistName("Dr. Amara Perera");
        sampleBillResponse.setTreatmentName("Routine Checkup");
        sampleBillResponse.setTreatmentCost(new BigDecimal("500.00"));
        sampleBillResponse.setConsultationFee(new BigDecimal("200.00"));
        sampleBillResponse.setTotalAmount(new BigDecimal("700.00"));
        sampleBillResponse.setBillDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/v1/bills/appointment/{id} - ADMIN can generate bill")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGenerateBill_Admin() throws Exception {
        when(billService.generateBill(1L)).thenReturn(sampleBillResponse);

        mockMvc.perform(post("/api/v1/bills/appointment/1").with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.billNumber").value("REC-000001"))
                .andExpect(jsonPath("$.data.totalAmount").value(700.00));
    }

    @Test
    @DisplayName("POST /api/v1/bills/appointment/{id} - RECEPTIONIST can generate bill")
    @WithMockUser(username = "recep1", roles = {"RECEPTIONIST"})
    void testGenerateBill_Receptionist() throws Exception {
        when(billService.generateBill(1L)).thenReturn(sampleBillResponse);

        mockMvc.perform(post("/api/v1/bills/appointment/1").with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.billNumber").value("REC-000001"));
    }

    @Test
    @DisplayName("POST /api/v1/bills/appointment/{id} - DENTIST cannot generate bill (403 Forbidden)")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testGenerateBill_Dentist_Forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/bills/appointment/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/v1/bills/{id} - ADMIN/RECEPTIONIST/DENTIST can view bill")
    @WithMockUser(username = "dentist1", roles = {"DENTIST"})
    void testGetBillById_Dentist() throws Exception {
        when(billService.getBillById(1L)).thenReturn(sampleBillResponse);

        mockMvc.perform(get("/api/v1/bills/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.billNumber").value("REC-000001"));
    }

    @Test
    @DisplayName("GET /api/v1/bills/appointment/{appointmentId} - view bill by appointment")
    @WithMockUser(username = "recep1", roles = {"RECEPTIONIST"})
    void testGetBillByAppointmentId() throws Exception {
        when(billService.getBillByAppointmentId(1L)).thenReturn(sampleBillResponse);

        mockMvc.perform(get("/api/v1/bills/appointment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.billNumber").value("REC-000001"));
    }

    @Test
    @DisplayName("POST /api/v1/bills/appointment/{id} - duplicate bill returns 400 Bad Request")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGenerateBill_Duplicate_Returns400() throws Exception {
        when(billService.generateBill(1L))
                .thenThrow(new CustomApiException("A bill has already been generated for appointment: APT-000001"));

        mockMvc.perform(post("/api/v1/bills/appointment/1").with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
