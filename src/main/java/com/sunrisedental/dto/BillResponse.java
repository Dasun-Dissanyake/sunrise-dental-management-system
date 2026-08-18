package com.sunrisedental.dto;

import com.sunrisedental.model.Bill;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Data Transfer Object for exposing Bill and Receipt details.
 */
public class BillResponse {

    private Long id;
    private String billNumber;
    private Long appointmentId;
    private String appointmentNumber;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String patientNumber;
    private String patientName;
    private String dentistName;
    private String treatmentName;
    private BigDecimal treatmentCost;
    private BigDecimal consultationFee;
    private BigDecimal totalAmount;
    private LocalDateTime billDate;

    public BillResponse() {
    }

    public static BillResponse fromBill(Bill bill) {
        if (bill == null) {
            return null;
        }

        BillResponse response = new BillResponse();
        response.setId(bill.getId());
        response.setBillNumber(bill.getBillNumber());
        response.setTreatmentCost(bill.getTreatmentCost());
        response.setConsultationFee(bill.getConsultationFee());
        response.setTotalAmount(bill.getTotalAmount());
        response.setBillDate(bill.getBillDate());

        if (bill.getAppointment() != null) {
            response.setAppointmentId(bill.getAppointment().getId());
            response.setAppointmentNumber(bill.getAppointment().getAppointmentNumber());
            response.setAppointmentDate(bill.getAppointment().getAppointmentDate());
            response.setAppointmentTime(bill.getAppointment().getAppointmentTime());

            if (bill.getAppointment().getPatient() != null) {
                response.setPatientNumber(bill.getAppointment().getPatient().getPatientNumber());
                response.setPatientName(bill.getAppointment().getPatient().getFullName());
            }

            if (bill.getAppointment().getDentist() != null) {
                response.setDentistName(bill.getAppointment().getDentist().getFullName());
            }

            if (bill.getAppointment().getTreatment() != null) {
                response.setTreatmentName(bill.getAppointment().getTreatment().getTreatmentName());
            }
        }

        return response;
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

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
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

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
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
