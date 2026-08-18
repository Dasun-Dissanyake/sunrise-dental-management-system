package com.sunrisedental.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Bill entity representing patient receipts and financial logs.
 */
@Entity
@Table(name = "bills", indexes = {
        @Index(name = "idx_bill_number", columnList = "bill_number"),
        @Index(name = "idx_bill_appointment_id", columnList = "appointment_id")
})
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bill number is required")
    @Column(name = "bill_number", nullable = false, unique = true, length = 20)
    private String billNumber;

    @NotNull(message = "Appointment is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @NotNull(message = "Treatment cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Treatment cost cannot be negative")
    @Column(name = "treatment_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal treatmentCost;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Consultation fee cannot be negative")
    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total amount cannot be negative")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @NotNull(message = "Bill date is required")
    @Column(name = "bill_date", nullable = false)
    private LocalDateTime billDate;

    public Bill() {
    }

    public Bill(String billNumber, Appointment appointment, BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal totalAmount, LocalDateTime billDate) {
        this.billNumber = billNumber;
        this.appointment = appointment;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
    }

    @PrePersist
    protected void onCreate() {
        if (this.billDate == null) {
            this.billDate = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }
}
