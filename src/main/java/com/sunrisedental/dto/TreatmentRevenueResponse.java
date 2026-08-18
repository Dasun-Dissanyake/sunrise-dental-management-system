package com.sunrisedental.dto;

import java.math.BigDecimal;

/**
 * DTO for treatment revenue report entries.
 */
public class TreatmentRevenueResponse {

    private String treatmentName;
    private long numberOfAppointments;
    private BigDecimal treatmentRevenue;

    public TreatmentRevenueResponse() {}

    public TreatmentRevenueResponse(String treatmentName, long numberOfAppointments, BigDecimal treatmentRevenue) {
        this.treatmentName = treatmentName;
        this.numberOfAppointments = numberOfAppointments;
        this.treatmentRevenue = treatmentRevenue;
    }

    // Getters and Setters
    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
    public long getNumberOfAppointments() { return numberOfAppointments; }
    public void setNumberOfAppointments(long numberOfAppointments) { this.numberOfAppointments = numberOfAppointments; }
    public BigDecimal getTreatmentRevenue() { return treatmentRevenue; }
    public void setTreatmentRevenue(BigDecimal treatmentRevenue) { this.treatmentRevenue = treatmentRevenue; }
}
