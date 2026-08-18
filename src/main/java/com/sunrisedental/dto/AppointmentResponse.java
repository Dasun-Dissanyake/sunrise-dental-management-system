package com.sunrisedental.dto;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Data Transfer Object for exposing full appointment details.
 */
public class AppointmentResponse {

    private Long id;
    private String appointmentNumber;

    private Long patientId;
    private String patientNumber;
    private String patientName;
    private String patientContact;

    private Long dentistId;
    private String dentistNumber;
    private String dentistName;
    private String dentistSpecialization;

    private Long treatmentId;
    private String treatmentCode;
    private String treatmentName;
    private BigDecimal treatmentCost;
    private BigDecimal consultationFee;

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AppointmentResponse() {
    }

    public static AppointmentResponse fromAppointment(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setAppointmentNumber(appointment.getAppointmentNumber());

        if (appointment.getPatient() != null) {
            response.setPatientId(appointment.getPatient().getId());
            response.setPatientNumber(appointment.getPatient().getPatientNumber());
            response.setPatientName(appointment.getPatient().getFullName());
            response.setPatientContact(appointment.getPatient().getContactNumber());
        }

        if (appointment.getDentist() != null) {
            response.setDentistId(appointment.getDentist().getId());
            response.setDentistNumber(appointment.getDentist().getDentistNumber());
            response.setDentistName(appointment.getDentist().getFullName());
            response.setDentistSpecialization(appointment.getDentist().getSpecialization());
        }

        if (appointment.getTreatment() != null) {
            response.setTreatmentId(appointment.getTreatment().getId());
            response.setTreatmentCode(appointment.getTreatment().getTreatmentCode());
            response.setTreatmentName(appointment.getTreatment().getTreatmentName());
            response.setTreatmentCost(appointment.getTreatment().getTreatmentCost());
            response.setConsultationFee(appointment.getTreatment().getConsultationFee());
        }

        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus());
        response.setNotes(appointment.getNotes());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());

        return response;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientContact() {
        return patientContact;
    }

    public void setPatientContact(String patientContact) {
        this.patientContact = patientContact;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
    }

    public String getDentistNumber() {
        return dentistNumber;
    }

    public void setDentistNumber(String dentistNumber) {
        this.dentistNumber = dentistNumber;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getDentistSpecialization() {
        return dentistSpecialization;
    }

    public void setDentistSpecialization(String dentistSpecialization) {
        this.dentistSpecialization = dentistSpecialization;
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Long treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public BigDecimal getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(BigDecimal treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
