package com.sunrisedental.service;

import com.sunrisedental.dto.BillResponse;

import java.util.List;

/**
 * Service interface for Bill and Receipt business operations.
 */
public interface BillService {

    BillResponse generateBill(Long appointmentId);

    BillResponse getBillById(Long id);

    BillResponse getBillByAppointmentId(Long appointmentId);

    List<BillResponse> getAllBills();
}
