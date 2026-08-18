package com.sunrisedental.controller;

import com.sunrisedental.dto.ApiResponse;
import com.sunrisedental.dto.DentistResponse;
import com.sunrisedental.service.DentistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Dentist catalog endpoints under /api/v1/dentists.
 */
@RestController
@RequestMapping("/api/v1/dentists")
public class DentistController {

    private final DentistService dentistService;

    public DentistController(DentistService dentistService) {
        this.dentistService = dentistService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DentistResponse>>> getAllDentists() {
        List<DentistResponse> dentists = dentistService.getAllDentists();
        return ResponseEntity.ok(new ApiResponse<>(true, "Dentists retrieved successfully", dentists));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DentistResponse>>> getActiveDentists() {
        List<DentistResponse> dentists = dentistService.getActiveDentists();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active dentists retrieved successfully", dentists));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DentistResponse>> getDentistById(@PathVariable Long id) {
        DentistResponse dentist = dentistService.getDentistById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Dentist retrieved successfully", dentist));
    }

    @GetMapping("/number/{dentistNumber}")
    public ResponseEntity<ApiResponse<DentistResponse>> getDentistByNumber(@PathVariable String dentistNumber) {
        DentistResponse dentist = dentistService.getDentistByNumber(dentistNumber);
        return ResponseEntity.ok(new ApiResponse<>(true, "Dentist retrieved successfully", dentist));
    }
}
