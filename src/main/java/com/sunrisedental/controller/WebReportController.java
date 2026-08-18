package com.sunrisedental.controller;

import com.sunrisedental.dto.*;
import com.sunrisedental.service.DentistService;
import com.sunrisedental.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Web MVC Controller serving Thymeleaf views for Reports and Analytics pages.
 */
@Controller
@RequestMapping("/reports")
public class WebReportController {

    private final ReportService reportService;
    private final DentistService dentistService;

    public WebReportController(ReportService reportService, DentistService dentistService) {
        this.reportService = reportService;
        this.dentistService = dentistService;
    }

    @GetMapping
    public String reportsDashboard() {
        return "reports";
    }

    @GetMapping("/appointments")
    public String dailyAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        if (date != null) {
            List<DailyAppointmentReportResponse> appointments = reportService.getDailyAppointments(date);
            model.addAttribute("appointments", appointments);
            model.addAttribute("reportGenerated", true);
        } else {
            model.addAttribute("appointments", Collections.emptyList());
            model.addAttribute("reportGenerated", false);
        }
        model.addAttribute("selectedDate", date);
        return "report-appointments";
    }

    @GetMapping("/revenue")
    public String revenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        if (startDate != null && endDate != null) {
            try {
                RevenueReportResponse revenue = reportService.getRevenueReport(startDate, endDate);
                model.addAttribute("revenue", revenue);
                model.addAttribute("reportGenerated", true);
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
                model.addAttribute("reportGenerated", false);
            }
        } else {
            model.addAttribute("reportGenerated", false);
        }
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "report-revenue";
    }

    @GetMapping("/dentists")
    public String dentistPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long dentistId,
            Model model) {
        List<DentistReportResponse> report;
        if (startDate != null && endDate != null) {
            report = reportService.getDentistPerformance(startDate, endDate, dentistId);
            model.addAttribute("reportGenerated", true);
        } else if (dentistId != null) {
            report = reportService.getDentistPerformance(null, null, dentistId);
            model.addAttribute("reportGenerated", true);
        } else {
            report = Collections.emptyList();
            model.addAttribute("reportGenerated", false);
        }
        model.addAttribute("dentistReport", report);
        model.addAttribute("dentists", dentistService.getActiveDentists());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("selectedDentistId", dentistId);
        return "report-dentists";
    }

    @GetMapping("/treatments")
    public String treatmentRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        if (startDate != null && endDate != null) {
            try {
                List<TreatmentRevenueResponse> report = reportService.getTreatmentRevenue(startDate, endDate);
                model.addAttribute("treatmentReport", report);
                model.addAttribute("reportGenerated", true);
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
                model.addAttribute("reportGenerated", false);
            }
        } else {
            model.addAttribute("reportGenerated", false);
        }
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "report-treatments";
    }
}
