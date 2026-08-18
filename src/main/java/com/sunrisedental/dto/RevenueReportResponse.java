package com.sunrisedental.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for revenue report including summary totals and detailed bill records.
 */
public class RevenueReportResponse {

    private long totalBills;
    private BigDecimal treatmentRevenue;
    private BigDecimal consultationRevenue;
    private BigDecimal totalRevenue;
    private List<BillDetail> bills;

    public RevenueReportResponse() {
        this.totalBills = 0;
        this.treatmentRevenue = BigDecimal.ZERO;
        this.consultationRevenue = BigDecimal.ZERO;
        this.totalRevenue = BigDecimal.ZERO;
        this.bills = new ArrayList<>();
    }

    // Getters and Setters
    public long getTotalBills() { return totalBills; }
    public void setTotalBills(long totalBills) { this.totalBills = totalBills; }
    public BigDecimal getTreatmentRevenue() { return treatmentRevenue; }
    public void setTreatmentRevenue(BigDecimal treatmentRevenue) { this.treatmentRevenue = treatmentRevenue; }
    public BigDecimal getConsultationRevenue() { return consultationRevenue; }
    public void setConsultationRevenue(BigDecimal consultationRevenue) { this.consultationRevenue = consultationRevenue; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public List<BillDetail> getBills() { return bills; }
    public void setBills(List<BillDetail> bills) { this.bills = bills; }

    /**
     * Inner DTO for individual bill line items in the revenue report.
     */
    public static class BillDetail {
        private String billNumber;
        private LocalDateTime billDate;
        private String patientName;
        private String treatmentName;
        private BigDecimal treatmentCost;
        private BigDecimal consultationFee;
        private BigDecimal totalAmount;

        public BillDetail() {}

        public BillDetail(String billNumber, LocalDateTime billDate, String patientName,
                          String treatmentName, BigDecimal treatmentCost, BigDecimal consultationFee, BigDecimal totalAmount) {
            this.billNumber = billNumber;
            this.billDate = billDate;
            this.patientName = patientName;
            this.treatmentName = treatmentName;
            this.treatmentCost = treatmentCost;
            this.consultationFee = consultationFee;
            this.totalAmount = totalAmount;
        }

        // Getters and Setters
        public String getBillNumber() { return billNumber; }
        public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
        public LocalDateTime getBillDate() { return billDate; }
        public void setBillDate(LocalDateTime billDate) { this.billDate = billDate; }
        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public String getTreatmentName() { return treatmentName; }
        public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
        public BigDecimal getTreatmentCost() { return treatmentCost; }
        public void setTreatmentCost(BigDecimal treatmentCost) { this.treatmentCost = treatmentCost; }
        public BigDecimal getConsultationFee() { return consultationFee; }
        public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    }
}
