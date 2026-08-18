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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebReportControllerTest {

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
    @DisplayName("GET /reports should render reports dashboard")
    void testReportsDashboard() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /reports/appointments should render daily appointments view")
    void testDailyAppointmentsView() throws Exception {
        DailyAppointmentReportResponse item = new DailyAppointmentReportResponse(
                "APT-000001", "Kavindu Perera", "Dr. Amara Perera", "Routine Checkup",
                LocalDate.of(2026, 8, 18), LocalTime.of(9, 0), "SCHEDULED"
        );
        when(reportService.getDailyAppointments(LocalDate.of(2026, 8, 18))).thenReturn(List.of(item));

        mockMvc.perform(get("/reports/appointments").param("date", "2026-08-18"))
                .andExpect(status().isOk())
                .andExpect(view().name("report-appointments"))
                .andExpect(model().attributeExists("appointments", "reportGenerated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /reports/revenue should render revenue report view")
    void testRevenueReportView() throws Exception {
        RevenueReportResponse res = new RevenueReportResponse();
        res.setTotalBills(1);
        res.setTotalRevenue(new BigDecimal("700.00"));
        when(reportService.getRevenueReport(eq(LocalDate.of(2026, 8, 1)), eq(LocalDate.of(2026, 8, 31))))
                .thenReturn(res);

        mockMvc.perform(get("/reports/revenue")
                        .param("startDate", "2026-08-01")
                        .param("endDate", "2026-08-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("report-revenue"))
                .andExpect(model().attributeExists("revenue", "reportGenerated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /reports/dentists should render dentist performance report view")
    void testDentistPerformanceView() throws Exception {
        when(dentistService.getActiveDentists()).thenReturn(Collections.emptyList());
        when(reportService.getDentistPerformance(any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/dentists")
                        .param("startDate", "2026-08-01")
                        .param("endDate", "2026-08-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("report-dentists"))
                .andExpect(model().attributeExists("dentistReport", "dentists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /reports/treatments should render treatment revenue report view")
    void testTreatmentRevenueView() throws Exception {
        when(reportService.getTreatmentRevenue(any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/treatments")
                        .param("startDate", "2026-08-01")
                        .param("endDate", "2026-08-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("report-treatments"))
                .andExpect(model().attributeExists("treatmentReport"));
    }
}