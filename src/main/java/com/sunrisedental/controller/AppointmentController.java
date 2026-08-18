package com.sunrisedental.controller;

import com.sunrisedental.dto.*;
import com.sunrisedental.model.AppointmentStatus;
import com.sunrisedental.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Appointment management endpoints under /api/v1/appointments.
 */
@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
            @Valid @RequestBody AppointmentCreateRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Appointment created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAllAppointments(
            @RequestParam(required = false) String status) {

        List<AppointmentResponse> appointments;
        if (status != null && !status.isBlank()) {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            appointments = appointmentService.getAppointmentsByStatus(appointmentStatus);
        } else {
            appointments = appointmentService.getAllAppointments();
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Appointments retrieved successfully", appointments));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getUpcomingAppointments() {
        List<AppointmentResponse> appointments = appointmentService.getUpcomingAppointments();
        return ResponseEntity.ok(new ApiResponse<>(true, "Upcoming appointments retrieved", appointments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Appointment retrieved successfully", appointment));
    }

    @GetMapping("/number/{appointmentNumber}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentByNumber(
            @PathVariable String appointmentNumber) {
        AppointmentResponse appointment = appointmentService.getAppointmentByNumber(appointmentNumber);
        return ResponseEntity.ok(new ApiResponse<>(true, "Appointment retrieved successfully", appointment));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> searchAppointments(
            @RequestParam(defaultValue = "") String query) {
        List<AppointmentResponse> appointments = appointmentService.searchAppointments(query);
        return ResponseEntity.ok(new ApiResponse<>(true, "Search completed", appointments));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateRequest request) {
        AppointmentResponse response = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Appointment updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusUpdateRequest request) {
        AppointmentResponse response = appointmentService.updateAppointmentStatus(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Appointment status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Appointment cancelled successfully", null));
    }
}
