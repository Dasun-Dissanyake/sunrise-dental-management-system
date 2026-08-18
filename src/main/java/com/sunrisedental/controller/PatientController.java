package com.sunrisedental.controller;

import com.sunrisedental.dto.ApiResponse;
import com.sunrisedental.dto.PatientCreateRequest;
import com.sunrisedental.dto.PatientResponse;
import com.sunrisedental.dto.PatientUpdateRequest;
import com.sunrisedental.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Web Service endpoints for Patient Management.
 */
@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(@Valid @RequestBody PatientCreateRequest request) {
        PatientResponse patientResponse = patientService.registerPatient(request);
        return new ResponseEntity<>(ApiResponse.success("Patient registered successfully", patientResponse), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAllPatients() {
        List<PatientResponse> patients = patientService.getAllPatients();
        return ResponseEntity.ok(ApiResponse.success("Patients retrieved successfully", patients));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(@PathVariable Long id) {
        PatientResponse patient = patientService.getPatientById(id);
        return ResponseEntity.ok(ApiResponse.success("Patient retrieved successfully", patient));
    }

    @GetMapping("/number/{patientNumber}")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientByNumber(@PathVariable String patientNumber) {
        PatientResponse patient = patientService.getPatientByNumber(patientNumber);
        return ResponseEntity.ok(ApiResponse.success("Patient retrieved successfully", patient));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> searchPatients(@RequestParam(value = "query", required = false) String query) {
        List<PatientResponse> results = patientService.searchPatients(query);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved", results));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientUpdateRequest request) {
        PatientResponse updatedPatient = patientService.updatePatient(id, request);
        return ResponseEntity.ok(ApiResponse.success("Patient updated successfully", updatedPatient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> deactivatePatient(@PathVariable Long id) {
        PatientResponse deactivatedPatient = patientService.deactivatePatient(id);
        return ResponseEntity.ok(ApiResponse.success("Patient deactivated successfully", deactivatedPatient));
    }
}
