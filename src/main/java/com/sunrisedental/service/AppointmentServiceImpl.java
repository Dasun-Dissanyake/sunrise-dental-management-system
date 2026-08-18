package com.sunrisedental.service;

import com.sunrisedental.dto.AppointmentCreateRequest;
import com.sunrisedental.dto.AppointmentResponse;
import com.sunrisedental.dto.AppointmentStatusUpdateRequest;
import com.sunrisedental.dto.AppointmentUpdateRequest;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.model.*;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.DentistRepository;
import com.sunrisedental.repository.PatientRepository;
import com.sunrisedental.repository.TreatmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Appointment business operations including
 * double-booking prevention, sequential number generation, and status management.
 */
@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                   PatientRepository patientRepository,
                                   DentistRepository dentistRepository,
                                   TreatmentRepository treatmentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentRepository = treatmentRepository;
    }

    @Override
    public synchronized AppointmentResponse createAppointment(AppointmentCreateRequest request) {
        // Validate patient exists
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new CustomApiException("Patient not found with ID: " + request.getPatientId()));

        if (!patient.isActive()) {
            throw new CustomApiException("Cannot book appointment for an inactive patient.");
        }

        // Validate dentist exists and is active
        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new CustomApiException("Dentist not found with ID: " + request.getDentistId()));

        if (!dentist.isActive()) {
            throw new CustomApiException("Cannot book appointment with inactive dentist: " + dentist.getFullName());
        }

        // Validate treatment exists and is active
        Treatment treatment = treatmentRepository.findById(request.getTreatmentId())
                .orElseThrow(() -> new CustomApiException("Treatment not found with ID: " + request.getTreatmentId()));

        if (!treatment.isActive()) {
            throw new CustomApiException("Cannot book appointment for inactive treatment: " + treatment.getTreatmentName());
        }

        // Double Booking Prevention
        boolean isDoubleBooked = appointmentRepository
                .existsByDentistIdAndAppointmentDateAndAppointmentTimeAndStatus(
                        dentist.getId(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime(),
                        AppointmentStatus.SCHEDULED
                );

        if (isDoubleBooked) {
            throw new CustomApiException(
                    "Dr. " + dentist.getFullName() + " already has an appointment scheduled at " +
                    request.getAppointmentDate() + " " + request.getAppointmentTime() +
                    ". Please select a different date or time."
            );
        }

        String appointmentNumber = generateUniqueAppointmentNumber();

        Appointment appointment = new Appointment(
                appointmentNumber,
                patient,
                dentist,
                treatment,
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                request.getNotes()
        );

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromAppointment(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("Appointment not found with ID: " + id));
        return AppointmentResponse.fromAppointment(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentByNumber(String appointmentNumber) {
        Appointment appointment = appointmentRepository.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new CustomApiException("Appointment not found with number: " + appointmentNumber));
        return AppointmentResponse.fromAppointment(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(AppointmentResponse::fromAppointment)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> searchAppointments(String query) {
        if (query == null || query.isBlank()) {
            return getAllAppointments();
        }
        return appointmentRepository.searchAppointments(query.trim()).stream()
                .map(AppointmentResponse::fromAppointment)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(AppointmentResponse::fromAppointment)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getUpcomingAppointments() {
        return appointmentRepository.findByAppointmentDateGreaterThanEqual(LocalDate.now()).stream()
                .map(AppointmentResponse::fromAppointment)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized AppointmentResponse updateAppointment(Long id, AppointmentUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("Appointment not found with ID: " + id));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new CustomApiException("Patient not found with ID: " + request.getPatientId()));

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new CustomApiException("Dentist not found with ID: " + request.getDentistId()));

        if (!dentist.isActive()) {
            throw new CustomApiException("Cannot book appointment with inactive dentist: " + dentist.getFullName());
        }

        Treatment treatment = treatmentRepository.findById(request.getTreatmentId())
                .orElseThrow(() -> new CustomApiException("Treatment not found with ID: " + request.getTreatmentId()));

        if (!treatment.isActive()) {
            throw new CustomApiException("Cannot book appointment for inactive treatment: " + treatment.getTreatmentName());
        }

        // Double booking prevention — exclude current appointment from check
        boolean isDoubleBooked = appointmentRepository
                .existsByDentistIdAndAppointmentDateAndAppointmentTimeAndStatusAndIdNot(
                        dentist.getId(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime(),
                        AppointmentStatus.SCHEDULED,
                        id
                );

        if (isDoubleBooked) {
            throw new CustomApiException(
                    "Dr. " + dentist.getFullName() + " already has an appointment scheduled at " +
                    request.getAppointmentDate() + " " + request.getAppointmentTime() +
                    ". Please select a different date or time."
            );
        }

        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setTreatment(treatment);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(request.getStatus());
        appointment.setNotes(request.getNotes());

        Appointment updated = appointmentRepository.save(appointment);
        return AppointmentResponse.fromAppointment(updated);
    }

    @Override
    public AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatusUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("Appointment not found with ID: " + id));

        appointment.setStatus(request.getStatus());
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            appointment.setNotes(request.getNotes());
        }

        Appointment updated = appointmentRepository.save(appointment);
        return AppointmentResponse.fromAppointment(updated);
    }

    @Override
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("Appointment not found with ID: " + id));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    /**
     * Generates a unique sequential appointment number in format APT-XXXXXX (e.g. APT-000001).
     * Uses max ID query with fallback collision detection to ensure uniqueness.
     */
    private String generateUniqueAppointmentNumber() {
        Long maxId = appointmentRepository.findMaxAppointmentId();
        long nextSequence = (maxId == null) ? 1 : maxId + 1;

        String candidate = String.format("APT-%06d", nextSequence);
        while (appointmentRepository.existsByAppointmentNumber(candidate)) {
            nextSequence++;
            candidate = String.format("APT-%06d", nextSequence);
        }
        return candidate;
    }
}
