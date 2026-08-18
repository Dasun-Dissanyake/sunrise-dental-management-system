package com.sunrisedental.service;

import com.sunrisedental.dto.PatientCreateRequest;
import com.sunrisedental.dto.PatientResponse;
import com.sunrisedental.dto.PatientUpdateRequest;

import java.util.List;

/**
 * Service interface for managing clinic patients.
 */
public interface PatientService {

    PatientResponse registerPatient(PatientCreateRequest request);

    PatientResponse getPatientById(Long id);

    PatientResponse getPatientByNumber(String patientNumber);

    List<PatientResponse> getAllPatients();

    List<PatientResponse> getActivePatients();

    List<PatientResponse> searchPatients(String query);

    PatientResponse updatePatient(Long id, PatientUpdateRequest request);

    PatientResponse deactivatePatient(Long id);
}
