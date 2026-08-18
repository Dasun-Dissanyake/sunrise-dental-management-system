package com.sunrisedental.repository;

import com.sunrisedental.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BillRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BillRepository billRepository;

    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        patient = new Patient("PAT-000001", "Kavindu Perera", "Colombo", "0771234567", null, LocalDate.of(1995, 5, 12), "Male");
        entityManager.persistAndFlush(patient);

        dentist = new Dentist("DENT-000001", "Dr. Amara Perera", "General Dentistry", "0771234567");
        entityManager.persistAndFlush(dentist);

        treatment = new Treatment("TRT-001", "Routine Checkup", "Standard checkup", new BigDecimal("500.00"), new BigDecimal("200.00"));
        entityManager.persistAndFlush(treatment);

        appointment = new Appointment("APT-000001", patient, dentist, treatment, LocalDate.now().plusDays(1), LocalTime.of(9, 0), "Routine checkup");
        entityManager.persistAndFlush(appointment);
    }

    @Test
    @DisplayName("Save and find bill by bill number")
    void testSaveAndFindByBillNumber() {
        Bill bill = new Bill("REC-000001", appointment, new BigDecimal("500.00"), new BigDecimal("200.00"), new BigDecimal("700.00"), LocalDateTime.now());
        entityManager.persistAndFlush(bill);

        Optional<Bill> found = billRepository.findByBillNumber("REC-000001");

        assertTrue(found.isPresent());
        assertEquals("REC-000001", found.get().getBillNumber());
        assertEquals(new BigDecimal("700.00"), found.get().getTotalAmount());
    }

    @Test
    @DisplayName("Find bill by appointment ID")
    void testFindByAppointmentId() {
        Bill bill = new Bill("REC-000001", appointment, new BigDecimal("500.00"), new BigDecimal("200.00"), new BigDecimal("700.00"), LocalDateTime.now());
        entityManager.persistAndFlush(bill);

        Optional<Bill> found = billRepository.findByAppointmentId(appointment.getId());

        assertTrue(found.isPresent());
        assertEquals("REC-000001", found.get().getBillNumber());
    }

    @Test
    @DisplayName("Exists by appointment ID")
    void testExistsByAppointmentId() {
        Bill bill = new Bill("REC-000001", appointment, new BigDecimal("500.00"), new BigDecimal("200.00"), new BigDecimal("700.00"), LocalDateTime.now());
        entityManager.persistAndFlush(bill);

        boolean exists = billRepository.existsByAppointmentId(appointment.getId());
        assertTrue(exists);
    }

    @Test
    @DisplayName("Find max bill ID")
    void testFindMaxBillId() {
        Bill bill = new Bill("REC-000001", appointment, new BigDecimal("500.00"), new BigDecimal("200.00"), new BigDecimal("700.00"), LocalDateTime.now());
        entityManager.persistAndFlush(bill);

        Long maxId = billRepository.findMaxBillId();
        assertNotNull(maxId);
    }
}
