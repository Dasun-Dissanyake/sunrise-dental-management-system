package com.sunrisedental.repository;

import com.sunrisedental.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Patient persistence operations.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by unique patient number.
     *
     * @param patientNumber the generated patient identifier (e.g. PAT-000001)
     * @return Optional containing the Patient if found
     */
    Optional<Patient> findByPatientNumber(String patientNumber);

    /**
     * Check whether a patient number already exists.
     *
     * @param patientNumber the patient number to check
     * @return true if exists, false otherwise
     */
    boolean existsByPatientNumber(String patientNumber);

    /**
     * Retrieve all active patients.
     *
     * @return List of active patients
     */
    List<Patient> findByActiveTrue();

    /**
     * Search patients by name, contact number, or patient number (case-insensitive).
     *
     * @param query search term
     * @return matching patients
     */
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.contactNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.patientNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Patient> searchPatients(@Param("query") String query);

    /**
     * Get the maximum primary key ID in the patients table to support sequential patient number generation.
     *
     * @return maximum ID or null if table is empty
     */
    @Query("SELECT MAX(p.id) FROM Patient p")
    Long findMaxPatientId();
}
