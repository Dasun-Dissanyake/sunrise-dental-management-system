package com.sunrisedental.service;

import com.sunrisedental.dto.PatientCreateRequest;
import com.sunrisedental.dto.PatientResponse;
import com.sunrisedental.dto.PatientUpdateRequest;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.model.Patient;
import com.sunrisedental.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing Patient entities and business workflows.
 */
@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public synchronized PatientResponse registerPatient(PatientCreateRequest request) {
        String generatedPatientNumber = generateUniquePatientNumber();

        Patient patient = new Patient(
                generatedPatientNumber,
                request.getFullName().trim(),
                request.getAddress().trim(),
                request.getContactNumber().trim(),
                request.getEmail() != null ? request.getEmail().trim() : null,
                request.getDateOfBirth(),
                request.getGender() != null ? request.getGender().trim() : null
        );

        Patient savedPatient = patientRepository.save(patient);
        return PatientResponse.fromPatient(savedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("Patient not found with ID: " + id));
        return PatientResponse.fromPatient(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByNumber(String patientNumber) {
        Patient patient = patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> new CustomApiException("Patient not found with patient number: " + patientNumber));
        return PatientResponse.fromPatient(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientResponse::fromPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getActivePatients() {
        return patientRepository.findByActiveTrue().stream()
                .map(PatientResponse::fromPatient)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatients(String query) {
        if (query == null || query.isBlank()) {
            return getAllPatients();
        }
        return patientRepository.searchPatients(query.trim()).stream()
                .map(PatientResponse::fromPatient)
                .collect(Collectors.toList());
    }

    @Override
    public PatientResponse updatePatient(Long id, PatientUpdateRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("Patient not found with ID: " + id));

        patient.setFullName(request.getFullName().trim());
        patient.setAddress(request.getAddress().trim());
        patient.setContactNumber(request.getContactNumber().trim());
        patient.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender() != null ? request.getGender().trim() : null);
        patient.setActive(request.isActive());

        Patient updatedPatient = patientRepository.save(patient);
        return PatientResponse.fromPatient(updatedPatient);
    }

    @Override
    public PatientResponse deactivatePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("Patient not found with ID: " + id));

        patient.setActive(false);
        Patient updatedPatient = patientRepository.save(patient);
        return PatientResponse.fromPatient(updatedPatient);
    }

    /**
     * Design Decision:
     * Generates a unique, non-repeating sequential patient number in the format PAT-XXXXXX (e.g. PAT-000001).
     * Uses max primary key ID query combined with fallback checks to ensure thread safety and no duplicates.
     */
    private String generateUniquePatientNumber() {
        Long maxId = patientRepository.findMaxPatientId();
        long nextSequence = (maxId == null) ? 1 : maxId + 1;

        String candidate = String.format("PAT-%06d", nextSequence);
        while (patientRepository.existsByPatientNumber(candidate)) {
            nextSequence++;
            candidate = String.format("PAT-%06d", nextSequence);
        }
        return candidate;
    }
}
