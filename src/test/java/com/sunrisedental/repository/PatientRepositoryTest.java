package com.sunrisedental.repository;

import com.sunrisedental.model.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    @DisplayName("Save patient and find by patient number")
    void testSaveAndFindByPatientNumber() {
        Patient patient = new Patient("PAT-000001", "Kavindu Perera", "Colombo 07", "0771234567", "kavindu@example.com", LocalDate.of(1995, 5, 12), "Male");
        entityManager.persistAndFlush(patient);

        Optional<Patient> found = patientRepository.findByPatientNumber("PAT-000001");

        assertTrue(found.isPresent());
        assertEquals("Kavindu Perera", found.get().getFullName());
        assertEquals("0771234567", found.get().getContactNumber());
    }

    @Test
    @DisplayName("Search patient by name")
    void testSearchPatientByName() {
        Patient patient1 = new Patient("PAT-000001", "Kamal Fernando", "Kandy", "0711111111", "kamal@test.com", LocalDate.of(1988, 10, 2), "Male");
        Patient patient2 = new Patient("PAT-000002", "Nimali Silva", "Galle", "0772222222", "nimali@test.com", LocalDate.of(1992, 3, 15), "Female");
        entityManager.persistAndFlush(patient1);
        entityManager.persistAndFlush(patient2);

        List<Patient> results = patientRepository.searchPatients("Kamal");

        assertEquals(1, results.size());
        assertEquals("PAT-000001", results.get(0).getPatientNumber());
    }

    @Test
    @DisplayName("Search patient by contact number")
    void testSearchPatientByContactNumber() {
        Patient patient = new Patient("PAT-000003", "Sunil Jayasinghe", "Negombo", "0779998887", null, null, null);
        entityManager.persistAndFlush(patient);

        List<Patient> results = patientRepository.searchPatients("0779998887");

        assertEquals(1, results.size());
        assertEquals("Sunil Jayasinghe", results.get(0).getFullName());
    }
}
