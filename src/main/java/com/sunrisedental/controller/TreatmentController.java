package com.sunrisedental.controller;

import com.sunrisedental.dto.ApiResponse;
import com.sunrisedental.dto.TreatmentResponse;
import com.sunrisedental.service.TreatmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Treatment catalog endpoints under /api/v1/treatments.
 */
@RestController
@RequestMapping("/api/v1/treatments")
public class TreatmentController {

    private final TreatmentService treatmentService;

    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TreatmentResponse>>> getAllTreatments() {
        List<TreatmentResponse> treatments = treatmentService.getAllTreatments();
        return ResponseEntity.ok(new ApiResponse<>(true, "Treatments retrieved successfully", treatments));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TreatmentResponse>>> getActiveTreatments() {
        List<TreatmentResponse> treatments = treatmentService.getActiveTreatments();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active treatments retrieved successfully", treatments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TreatmentResponse>> getTreatmentById(@PathVariable Long id) {
        TreatmentResponse treatment = treatmentService.getTreatmentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Treatment retrieved successfully", treatment));
    }

    @GetMapping("/code/{treatmentCode}")
    public ResponseEntity<ApiResponse<TreatmentResponse>> getTreatmentByCode(@PathVariable String treatmentCode) {
        TreatmentResponse treatment = treatmentService.getTreatmentByCode(treatmentCode);
        return ResponseEntity.ok(new ApiResponse<>(true, "Treatment retrieved successfully", treatment));
    }
}
