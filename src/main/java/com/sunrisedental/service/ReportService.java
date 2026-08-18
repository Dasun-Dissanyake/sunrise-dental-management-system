package com.sunrisedental.service;

import com.sunrisedental.dto.DailyAppointmentReportResponse;
import com.sunrisedental.dto.DentistReportResponse;
import com.sunrisedental.dto.RevenueReportResponse;
import com.sunrisedental.dto.TreatmentRevenueResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Report and Analytics business operations.
 */
public interface ReportService {

    List<DailyAppointmentReportResponse> getDailyAppointments(LocalDate date);

    RevenueReportResponse getRevenueReport(LocalDate startDate, LocalDate endDate);

    List<DentistReportResponse> getDentistPerformance(LocalDate startDate, LocalDate endDate, Long dentistId);

    List<TreatmentRevenueResponse> getTreatmentRevenue(LocalDate startDate, LocalDate endDate);
}
