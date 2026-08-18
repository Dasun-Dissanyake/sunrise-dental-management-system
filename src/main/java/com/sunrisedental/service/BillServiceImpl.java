package com.sunrisedental.service;

import com.sunrisedental.dto.BillResponse;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.model.Treatment;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Bill and Receipt management workflows.
 */
@Service
@Transactional
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;

    public BillServiceImpl(BillRepository billRepository, AppointmentRepository appointmentRepository) {
        this.billRepository = billRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public synchronized BillResponse generateBill(Long appointmentId) {
        if (appointmentId == null) {
            throw new CustomApiException("Appointment ID cannot be null.");
        }

        // Validate appointment exists
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new CustomApiException("Appointment not found with ID: " + appointmentId));

        // Check if bill already exists for this appointment
        if (billRepository.existsByAppointmentId(appointmentId)) {
            throw new CustomApiException("A bill has already been generated for appointment: " + appointment.getAppointmentNumber());
        }

        // Retrieve treatment details
        Treatment treatment = appointment.getTreatment();
        if (treatment == null) {
            throw new CustomApiException("Treatment details missing for appointment: " + appointment.getAppointmentNumber());
        }

        BigDecimal treatmentCost = treatment.getTreatmentCost();
        BigDecimal consultationFee = treatment.getConsultationFee();

        if (treatmentCost == null || treatmentCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomApiException("Invalid treatment cost. Cost cannot be negative or null.");
        }

        if (consultationFee == null || consultationFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomApiException("Invalid consultation fee. Fee cannot be negative or null.");
        }

        // Total Amount Calculation: Treatment Cost + Consultation Fee
        BigDecimal totalAmount = treatmentCost.add(consultationFee);

        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomApiException("Total bill amount cannot be negative.");
        }

        String billNumber = generateUniqueBillNumber();

        Bill bill = new Bill(
                billNumber,
                appointment,
                treatmentCost,
                consultationFee,
                totalAmount,
                LocalDateTime.now()
        );

        Bill savedBill = billRepository.save(bill);
        return BillResponse.fromBill(savedBill);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("Bill not found with ID: " + id));
        return BillResponse.fromBill(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse getBillByAppointmentId(Long appointmentId) {
        Bill bill = billRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new CustomApiException("Bill not found for appointment ID: " + appointmentId));
        return BillResponse.fromBill(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getAllBills() {
        return billRepository.findAll().stream()
                .map(BillResponse::fromBill)
                .collect(Collectors.toList());
    }

    /**
     * Generates a unique sequential bill number in format REC-XXXXXX (e.g. REC-000001).
     */
    private String generateUniqueBillNumber() {
        Long maxId = billRepository.findMaxBillId();
        long nextSequence = (maxId == null) ? 1 : maxId + 1;

        String candidate = String.format("REC-%06d", nextSequence);
        while (billRepository.existsByBillNumber(candidate)) {
            nextSequence++;
            candidate = String.format("REC-%06d", nextSequence);
        }
        return candidate;
    }
}
