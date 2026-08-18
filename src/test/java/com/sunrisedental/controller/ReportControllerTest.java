package com.sunrisedental.controller;

import com.sunrisedental.dto.DailyAppointmentReportResponse;
import com.sunrisedental.dto.DentistReportResponse;
import com.sunrisedental.dto.RevenueReportResponse;
import com.sunrisedental.dto.TreatmentRevenueResponse;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportControllerTest {

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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/reports/appointments/daily should return daily appointments")
    void testGetDailyAppointments() throws Exception {
        DailyAppointmentReportResponse item = new DailyAppointmentReportResponse(
                "APT-000001", "Kavindu Perera", "Dr. Amara Perera", "Routine Checkup",
                LocalDate.of(2026, 8, 18), LocalTime.of(9, 0), "SCHEDULED"
        );
        when(reportService.getDailyAppointments(any(LocalDate.class))).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/reports/appointments/daily")
                        .param("date", "2026-08-18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].appointmentNumber").value("APT-000001"))
                .andExpect(jsonPath("$.data[0].patientName").value("Kavindu Perera"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/reports/revenue should return revenue summary")
    void testGetRevenueReport() throws Exception {
        RevenueReportResponse res = new RevenueReportResponse();
        res.setTotalBills(1);
        res.setTotalRevenue(new BigDecimal("700.00"));
        when(reportService.getRevenueReport(eq(LocalDate.of(2026, 8, 1)), eq(LocalDate.of(2026, 8, 31))))
                .thenReturn(res);

        mockMvc.perform(get("/api/v1/reports/revenue")
                        .param("startDate", "2026-08-01")
                        .param("endDate", "2026-08-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalBills").value(1))
                .andExpect(jsonPath("$.data.totalRevenue").value(700.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/reports/dentists should return dentist performance")
    void testGetDentistPerformance() throws Exception {
        DentistReportResponse item = new DentistReportResponse("Dr. Amara Perera", 5, 4, 1, 0, 0);
        when(reportService.getDentistPerformance(any(), any(), any())).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/reports/dentists")
                        .param("startDate", "2026-08-01")
                        .param("endDate", "2026-08-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].dentistName").value("Dr. Amara Perera"))
                .andExpect(jsonPath("$.data[0].totalAppointments").value(5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/reports/treatments should return treatment revenue")
    void testGetTreatmentRevenue() throws Exception {
        TreatmentRevenueResponse item = new TreatmentRevenueResponse("Routine Checkup", 3, new BigDecimal("2100.00"));
        when(reportService.getTreatmentRevenue(eq(LocalDate.of(2026, 8, 1)), eq(LocalDate.of(2026, 8, 31))))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/reports/treatments")
                        .param("startDate", "2026-08-01")
                        .param("endDate", "2026-08-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].treatmentName").value("Routine Checkup"))
                .andExpect(jsonPath("$.data[0].treatmentRevenue").value(2100.00));
    }
}