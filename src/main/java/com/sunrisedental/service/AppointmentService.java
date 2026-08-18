package com.sunrisedental.service;

import com.sunrisedental.dto.AppointmentCreateRequest;
import com.sunrisedental.dto.AppointmentResponse;
import com.sunrisedental.dto.AppointmentStatusUpdateRequest;
import com.sunrisedental.dto.AppointmentUpdateRequest;
import com.sunrisedental.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Appointment business operations.
 */
public interface AppointmentService {

    AppointmentResponse createAppointment(AppointmentCreateRequest request);

    AppointmentResponse getAppointmentById(Long id);

    AppointmentResponse getAppointmentByNumber(String appointmentNumber);

    List<AppointmentResponse> getAllAppointments();

    List<AppointmentResponse> searchAppointments(String query);

    List<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status);

    List<AppointmentResponse> getUpcomingAppointments();

    AppointmentResponse updateAppointment(Long id, AppointmentUpdateRequest request);

    AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatusUpdateRequest request);

    void cancelAppointment(Long id);
}
