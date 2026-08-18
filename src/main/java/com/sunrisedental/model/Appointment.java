package com.sunrisedental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * JPA Appointment entity representing scheduled dental clinic appointments.
 */
@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_apt_number", columnList = "appointment_number"),
        @Index(name = "idx_dentist_date_time", columnList = "dentist_id, appointment_date, appointment_time"),
        @Index(name = "idx_patient_id", columnList = "patient_id")
})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_number", nullable = false, unique = true, length = 20)
    private String appointmentNumber;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Dentist is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;

    @NotNull(message = "Treatment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @NotNull(message = "Appointment date is required")
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @NotNull(message = "Appointment status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Appointment() {
    }

    public Appointment(String appointmentNumber, Patient patient, Dentist dentist, Treatment treatment,
                       LocalDate appointmentDate, LocalTime appointmentTime, String notes) {
        this.appointmentNumber = appointmentNumber;
        this.patient = patient;
        this.dentist = dentist;
        this.treatment = treatment;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.notes = notes;
        this.status = AppointmentStatus.SCHEDULED;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = AppointmentStatus.SCHEDULED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public void setDentist(Dentist dentist) {
        this.dentist = dentist;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
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
