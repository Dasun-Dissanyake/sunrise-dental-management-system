package com.sunrisedental.service;

import com.sunrisedental.dto.*;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;
import com.sunrisedental.model.Bill;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.BillRepository;
import com.sunrisedental.repository.DentistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for Report and Analytics operations.
 */
@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;
    private final DentistRepository dentistRepository;

    public ReportServiceImpl(AppointmentRepository appointmentRepository,
                             BillRepository billRepository,
                             DentistRepository dentistRepository) {
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
        this.dentistRepository = dentistRepository;
    }

    @Override
    public List<DailyAppointmentReportResponse> getDailyAppointments(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<Appointment> appointments = appointmentRepository.findByAppointmentDate(date);
        return appointments.stream()
                .map(a -> new DailyAppointmentReportResponse(
                        a.getAppointmentNumber(),
                        a.getPatient().getFullName(),
                        a.getDentist().getFullName(),
                        a.getTreatment().getTreatmentName(),
                        a.getAppointmentDate(),
                        a.getAppointmentTime(),
                        a.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public RevenueReportResponse getRevenueReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new com.sunrisedental.exception.CustomApiException("Start date and end date are required.");
        }
        if (startDate.isAfter(endDate)) {
            throw new com.sunrisedental.exception.CustomApiException("Start date cannot be after end date.");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Bill> bills = billRepository.findByBillDateBetween(startDateTime, endDateTime);

        RevenueReportResponse response = new RevenueReportResponse();
        response.setTotalBills(bills.size());

        BigDecimal treatmentRevenue = BigDecimal.ZERO;
        BigDecimal consultationRevenue = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        List<RevenueReportResponse.BillDetail> billDetails = new ArrayList<>();

        for (Bill bill : bills) {
            treatmentRevenue = treatmentRevenue.add(bill.getTreatmentCost());
            consultationRevenue = consultationRevenue.add(bill.getConsultationFee());
            totalRevenue = totalRevenue.add(bill.getTotalAmount());

            String patientName = "";
            String treatmentName = "";
            if (bill.getAppointment() != null) {
                if (bill.getAppointment().getPatient() != null) {
                    patientName = bill.getAppointment().getPatient().getFullName();
                }
                if (bill.getAppointment().getTreatment() != null) {
                    treatmentName = bill.getAppointment().getTreatment().getTreatmentName();
                }
            }

            billDetails.add(new RevenueReportResponse.BillDetail(
                    bill.getBillNumber(),
                    bill.getBillDate(),
                    patientName,
                    treatmentName,
                    bill.getTreatmentCost(),
                    bill.getConsultationFee(),
                    bill.getTotalAmount()
            ));
        }

        response.setTreatmentRevenue(treatmentRevenue);
        response.setConsultationRevenue(consultationRevenue);
        response.setTotalRevenue(totalRevenue);
        response.setBills(billDetails);

        return response;
    }

    @Override
    public List<DentistReportResponse> getDentistPerformance(LocalDate startDate, LocalDate endDate, Long dentistId) {
        List<Appointment> appointments;
        if (startDate != null && endDate != null) {
            appointments = appointmentRepository.findByAppointmentDateBetween(startDate, endDate);
        } else {
            appointments = appointmentRepository.findAll();
        }

        if (dentistId != null) {
            appointments = appointments.stream()
                    .filter(a -> a.getDentist() != null && a.getDentist().getId().equals(dentistId))
                    .collect(Collectors.toList());
        }

        Map<String, List<Appointment>> grouped = appointments.stream()
                .filter(a -> a.getDentist() != null)
                .collect(Collectors.groupingBy(a -> a.getDentist().getFullName()));

        return grouped.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<Appointment> apts = entry.getValue();
                    long total = apts.size();
                    long completed = apts.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
                    long cancelled = apts.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count();
                    long scheduled = apts.stream().filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED).count();
                    long noShow = apts.stream().filter(a -> a.getStatus() == AppointmentStatus.NO_SHOW).count();
                    return new DentistReportResponse(name, total, completed, cancelled, scheduled, noShow);
                })
                .sorted(Comparator.comparingLong(DentistReportResponse::getTotalAppointments).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<TreatmentRevenueResponse> getTreatmentRevenue(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new com.sunrisedental.exception.CustomApiException("Start date and end date are required.");
        }
        if (startDate.isAfter(endDate)) {
            throw new com.sunrisedental.exception.CustomApiException("Start date cannot be after end date.");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Bill> bills = billRepository.findByBillDateBetween(startDateTime, endDateTime);

        Map<String, List<Bill>> grouped = bills.stream()
                .filter(b -> b.getAppointment() != null && b.getAppointment().getTreatment() != null)
                .collect(Collectors.groupingBy(b -> b.getAppointment().getTreatment().getTreatmentName()));

        return grouped.entrySet().stream()
                .map(entry -> {
                    String treatmentName = entry.getKey();
                    List<Bill> treatmentBills = entry.getValue();
                    long count = treatmentBills.size();
                    BigDecimal revenue = treatmentBills.stream()
                            .map(Bill::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new TreatmentRevenueResponse(treatmentName, count, revenue);
                })
                .sorted(Comparator.comparing(TreatmentRevenueResponse::getTreatmentRevenue).reversed())
                .collect(Collectors.toList());
    }
}
