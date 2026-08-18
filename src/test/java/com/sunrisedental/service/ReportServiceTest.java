package com.sunrisedental.service;

import com.sunrisedental.dto.DailyAppointmentReportResponse;
import com.sunrisedental.dto.DentistReportResponse;
import com.sunrisedental.dto.RevenueReportResponse;
import com.sunrisedental.dto.TreatmentRevenueResponse;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.model.*;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.BillRepository;
import com.sunrisedental.repository.DentistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private DentistRepository dentistRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;
    private Appointment appointment;
    private Bill bill;

    @BeforeEach
    void setUp() {
        patient = new Patient("PAT-000001", "Kavindu Perera", "Colombo", "0771234567", null, LocalDate.of(1995, 5, 12), "Male");
        patient.setId(1L);

        dentist = new Dentist("DENT-000001", "Dr. Amara Perera", "General Dentistry", "0771234567");
        dentist.setId(1L);

        treatment = new Treatment("TRT-001", "Routine Checkup", "Standard checkup", new BigDecimal("500.00"), new BigDecimal("200.00"));
        treatment.setId(1L);

        appointment = new Appointment("APT-000001", patient, dentist, treatment, LocalDate.of(2026, 8, 18), LocalTime.of(9, 0), "Notes");
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        bill = new Bill("REC-000001", appointment, new BigDecimal("500.00"), new BigDecimal("200.00"), new BigDecimal("700.00"), LocalDateTime.of(2026, 8, 18, 10, 0));
        bill.setId(1L);
    }

    @Test
    @DisplayName("getDailyAppointments should return appointments for given date")
    void testGetDailyAppointments() {
        LocalDate date = LocalDate.of(2026, 8, 18);
        when(appointmentRepository.findByAppointmentDate(date)).thenReturn(List.of(appointment));

        List<DailyAppointmentReportResponse> result = reportService.getDailyAppointments(date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("APT-000001", result.get(0).getAppointmentNumber());
        assertEquals("Kavindu Perera", result.get(0).getPatientName());
        assertEquals("Dr. Amara Perera", result.get(0).getDentistName());
        assertEquals("Routine Checkup", result.get(0).getTreatmentName());
        assertEquals("COMPLETED", result.get(0).getStatus());
    }

    @Test
    @DisplayName("getDailyAppointments should default to today when date is null")
    void testGetDailyAppointments_NullDate() {
        when(appointmentRepository.findByAppointmentDate(any(LocalDate.class))).thenReturn(Collections.emptyList());

        List<DailyAppointmentReportResponse> result = reportService.getDailyAppointments(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appointmentRepository).findByAppointmentDate(LocalDate.now());
    }

    @Test
    @DisplayName("getRevenueReport should return aggregated revenue for date range")
    void testGetRevenueReport() {
        LocalDate startDate = LocalDate.of(2026, 8, 1);
        LocalDate endDate = LocalDate.of(2026, 8, 31);
        when(billRepository.findByBillDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(bill));

        RevenueReportResponse result = reportService.getRevenueReport(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.getTotalBills());
        assertEquals(new BigDecimal("500.00"), result.getTreatmentRevenue());
        assertEquals(new BigDecimal("200.00"), result.getConsultationRevenue());
        assertEquals(new BigDecimal("700.00"), result.getTotalRevenue());
        assertEquals(1, result.getBills().size());
        assertEquals("REC-000001", result.getBills().get(0).getBillNumber());
    }

    @Test
    @DisplayName("getRevenueReport should throw exception when startDate is after endDate")
    void testGetRevenueReport_InvalidDateRange() {
        LocalDate startDate = LocalDate.of(2026, 8, 31);
        LocalDate endDate = LocalDate.of(2026, 8, 1);

        assertThrows(CustomApiException.class, () -> reportService.getRevenueReport(startDate, endDate));
    }

    @Test
    @DisplayName("getRevenueReport should throw exception when dates are null")
    void testGetRevenueReport_NullDates() {
        assertThrows(CustomApiException.class, () -> reportService.getRevenueReport(null, LocalDate.now()));
    }

    @Test
    @DisplayName("getDentistPerformance should group and calculate performance statistics")
    void testGetDentistPerformance() {
        LocalDate startDate = LocalDate.of(2026, 8, 1);
        LocalDate endDate = LocalDate.of(2026, 8, 31);

        Appointment apt2 = new Appointment("APT-000002", patient, dentist, treatment, LocalDate.of(2026, 8, 19), LocalTime.of(10, 0), null);
        apt2.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findByAppointmentDateBetween(startDate, endDate))
                .thenReturn(Arrays.asList(appointment, apt2));

        List<DentistReportResponse> result = reportService.getDentistPerformance(startDate, endDate, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dr. Amara Perera", result.get(0).getDentistName());
        assertEquals(2, result.get(0).getTotalAppointments());
        assertEquals(1, result.get(0).getCompleted());
        assertEquals(1, result.get(0).getCancelled());
    }

    @Test
    @DisplayName("getTreatmentRevenue should aggregate revenue by treatment")
    void testGetTreatmentRevenue() {
        LocalDate startDate = LocalDate.of(2026, 8, 1);
        LocalDate endDate = LocalDate.of(2026, 8, 31);
        when(billRepository.findByBillDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(bill));

        List<TreatmentRevenueResponse> result = reportService.getTreatmentRevenue(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Routine Checkup", result.get(0).getTreatmentName());
        assertEquals(1, result.get(0).getNumberOfAppointments());
        assertEquals(new BigDecimal("700.00"), result.get(0).getTreatmentRevenue());
    }
}