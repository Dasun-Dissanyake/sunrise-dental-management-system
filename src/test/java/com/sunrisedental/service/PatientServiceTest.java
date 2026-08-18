package com.sunrisedental.service;

import com.sunrisedental.dto.PatientCreateRequest;
import com.sunrisedental.dto.PatientResponse;
import com.sunrisedental.dto.PatientUpdateRequest;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.model.Patient;
import com.sunrisedental.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    @DisplayName("Register patient successfully and auto-generate PAT-000001")
    void testRegisterPatient_Success() {
        PatientCreateRequest request = new PatientCreateRequest("Nimal Perera", "Colombo 03", "0771234567", "nimal@example.com", LocalDate.of(1990, 1, 1), "Male");
        Patient savedPatient = new Patient("PAT-000001", "Nimal Perera", "Colombo 03", "0771234567", "nimal@example.com", LocalDate.of(1990, 1, 1), "Male");
        savedPatient.setId(1L);

        when(patientRepository.findMaxPatientId()).thenReturn(null);
        when(patientRepository.existsByPatientNumber("PAT-000001")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        PatientResponse response = patientService.registerPatient(request);

        assertNotNull(response);
        assertEquals("PAT-000001", response.getPatientNumber());
        assertEquals("Nimal Perera", response.getFullName());
    }

    @Test
    @DisplayName("Get patient by ID successfully")
    void testGetPatientById_Success() {
        Patient patient = new Patient("PAT-000001", "Amal Silva", "Dehiwala", "0777654321", null, null, null);
        patient.setId(1L);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientResponse response = patientService.getPatientById(1L);

        assertNotNull(response);
        assertEquals("PAT-000001", response.getPatientNumber());
    }

    @Test
    @DisplayName("Get patient by patient number successfully")
    void testGetPatientByNumber_Success() {
        Patient patient = new Patient("PAT-000002", "Ruwan Gamage", "Galle", "0712345678", null, null, null);
        when(patientRepository.findByPatientNumber("PAT-000002")).thenReturn(Optional.of(patient));

        PatientResponse response = patientService.getPatientByNumber("PAT-000002");

        assertNotNull(response);
        assertEquals("Ruwan Gamage", response.getFullName());
    }

    @Test
    @DisplayName("Search patients calls repository search method")
    void testSearchPatients() {
        Patient patient = new Patient("PAT-000001", "Saman Kumara", "Kandy", "0771234567", null, null, null);
        when(patientRepository.searchPatients("Saman")).thenReturn(Collections.singletonList(patient));

        List<PatientResponse> results = patientService.searchPatients("Saman");

        assertEquals(1, results.size());
        assertEquals("Saman Kumara", results.get(0).getFullName());
    }

    @Test
    @DisplayName("Update patient demographics successfully")
    void testUpdatePatient_Success() {
        Patient patient = new Patient("PAT-000001", "Old Name", "Old Address", "0771111111", null, null, null);
        patient.setId(1L);

        PatientUpdateRequest updateRequest = new PatientUpdateRequest("New Name", "New Address", "0772222222", "new@example.com", null, "Male", true);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> i.getArgument(0));

        PatientResponse updated = patientService.updatePatient(1L, updateRequest);

        assertEquals("New Name", updated.getFullName());
        assertEquals("New Address", updated.getAddress());
        assertEquals("0772222222", updated.getContactNumber());
    }

    @Test
    @DisplayName("Deactivate patient soft-deletes record (active = false)")
    void testDeactivatePatient_Success() {
        Patient patient = new Patient("PAT-000001", "Kasun", "Colombo", "0771234567", null, null, null);
        patient.setId(1L);
        patient.setActive(true);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> i.getArgument(0));

        PatientResponse deactivated = patientService.deactivatePatient(1L);

        assertFalse(deactivated.isActive());
        verify(patientRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Cannot update non-existent patient throws CustomApiException")
    void testUpdateNonExistentPatient_ThrowsException() {
        PatientUpdateRequest updateRequest = new PatientUpdateRequest("Name", "Address", "0771234567", null, null, null, true);
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomApiException.class, () -> patientService.updatePatient(99L, updateRequest));
    }

    @Test
    @DisplayName("Cannot deactivate non-existent patient throws CustomApiException")
    void testDeactivateNonExistentPatient_ThrowsException() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomApiException.class, () -> patientService.deactivatePatient(99L));
    }
}
