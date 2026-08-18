package com.sunrisedental.controller;

import com.sunrisedental.dto.ApiResponse;
import com.sunrisedental.dto.BillResponse;
import com.sunrisedental.service.BillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Billing and Receipt endpoints under /api/v1/bills.
 */
@RestController
@RequestMapping("/api/v1/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<BillResponse>> generateBill(@PathVariable Long appointmentId) {
        BillResponse billResponse = billService.generateBill(appointmentId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Bill generated successfully", billResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BillResponse>> getBillById(@PathVariable Long id) {
        BillResponse billResponse = billService.getBillById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bill retrieved successfully", billResponse));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<BillResponse>> getBillByAppointmentId(@PathVariable Long appointmentId) {
        BillResponse billResponse = billService.getBillByAppointmentId(appointmentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bill retrieved successfully", billResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BillResponse>>> getAllBills() {
        List<BillResponse> bills = billService.getAllBills();
        return ResponseEntity.ok(new ApiResponse<>(true, "Bills retrieved successfully", bills));
    }
}
