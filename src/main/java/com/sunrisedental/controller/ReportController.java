package com.sunrisedental.controller;

import com.sunrisedental.dto.*;
import com.sunrisedental.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller exposing Report and Analytics endpoints under /api/v1/reports.
 */
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/appointments/daily")
    public ResponseEntity<ApiResponse<List<DailyAppointmentReportResponse>>> getDailyAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<DailyAppointmentReportResponse> data = reportService.getDailyAppointments(date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Daily appointments report retrieved", data));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        RevenueReportResponse data = reportService.getRevenueReport(startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Revenue report retrieved", data));
    }

    @GetMapping("/dentists")
    public ResponseEntity<ApiResponse<List<DentistReportResponse>>> getDentistPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long dentistId) {
        List<DentistReportResponse> data = reportService.getDentistPerformance(startDate, endDate, dentistId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Dentist performance report retrieved", data));
    }

    @GetMapping("/treatments")
    public ResponseEntity<ApiResponse<List<TreatmentRevenueResponse>>> getTreatmentRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TreatmentRevenueResponse> data = reportService.getTreatmentRevenue(startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Treatment revenue report retrieved", data));
    }
}
