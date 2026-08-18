package com.sunrisedental.dto;

import com.sunrisedental.model.Treatment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Treatment catalog details.
 */
public class TreatmentResponse {

    private Long id;
    private String treatmentCode;
    private String treatmentName;
    private String description;
    private BigDecimal treatmentCost;
    private BigDecimal consultationFee;
    private boolean active;
    private LocalDateTime createdAt;

    public TreatmentResponse() {
    }

    public TreatmentResponse(Long id, String treatmentCode, String treatmentName, String description, BigDecimal treatmentCost, BigDecimal consultationFee, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.treatmentCode = treatmentCode;
        this.treatmentName = treatmentName;
        this.description = description;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static TreatmentResponse fromTreatment(Treatment treatment) {
        if (treatment == null) {
            return null;
        }
        return new TreatmentResponse(
                treatment.getId(),
                treatment.getTreatmentCode(),
                treatment.getTreatmentName(),
                treatment.getDescription(),
                treatment.getTreatmentCost(),
                treatment.getConsultationFee(),
                treatment.isActive(),
                treatment.getCreatedAt()
        );
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
