package com.sunrisedental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Treatment entity representing dental procedures and cataloged treatments.
 */
@Entity
@Table(name = "treatments", indexes = {
        @Index(name = "idx_treatment_code", columnList = "treatment_code")
})
public class Treatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Treatment code is required")
    @Size(min = 2, max = 20, message = "Treatment code must be between 2 and 20 characters")
    @Column(name = "treatment_code", nullable = false, unique = true, length = 20)
    private String treatmentCode;

    @NotBlank(message = "Treatment name is required")
    @Size(min = 2, max = 100, message = "Treatment name must be between 2 and 100 characters")
    @Column(name = "treatment_name", nullable = false, length = 100)
    private String treatmentName;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Treatment cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Treatment cost cannot be negative")
    @Column(name = "treatment_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal treatmentCost;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Consultation fee cannot be negative")
    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Treatment() {
    }

    public Treatment(String treatmentCode, String treatmentName, String description, BigDecimal treatmentCost, BigDecimal consultationFee) {
        this.treatmentCode = treatmentCode;
        this.treatmentName = treatmentName;
        this.description = description;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
