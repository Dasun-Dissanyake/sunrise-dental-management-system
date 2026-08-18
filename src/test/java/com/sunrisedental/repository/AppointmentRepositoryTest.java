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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AppointmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;

    @BeforeEach
    void setUp() {
        patient = new Patient("PAT-000001", "Kavindu Perera", "Colombo", "0771234567", null, LocalDate.of(1995, 5, 12), "Male");
        entityManager.persistAndFlush(patient);

        dentist = new Dentist("DENT-000001", "Dr. Amara Perera", "General Dentistry", "0771234567");
        entityManager.persistAndFlush(dentist);

        treatment = new Treatment("TRT-001", "Routine Checkup", "Standard checkup", new BigDecimal("500.00"), new BigDecimal("200.00"));
        entityManager.persistAndFlush(treatment);
    }

    @Test
    @DisplayName("Save and find appointment by appointment number")
    void testSaveAndFindByAppointmentNumber() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment,
                LocalDate.now().plusDays(1), LocalTime.of(9, 0), null);
        entityManager.persistAndFlush(apt);

        Optional<Appointment> found = appointmentRepository.findByAppointmentNumber("APT-000001");

        assertTrue(found.isPresent());
        assertEquals("APT-000001", found.get().getAppointmentNumber());
    }

    @Test
    @DisplayName("Find appointments by status")
    void testFindByStatus() {
        Appointment apt1 = new Appointment("APT-000001", patient, dentist, treatment,
                LocalDate.now().plusDays(1), LocalTime.of(9, 0), null);
        Appointment apt2 = new Appointment("APT-000002", patient, dentist, treatment,
                LocalDate.now().plusDays(2), LocalTime.of(10, 0), null);
        apt2.setStatus(AppointmentStatus.COMPLETED);
        entityManager.persistAndFlush(apt1);
        entityManager.persistAndFlush(apt2);

        List<Appointment> scheduled = appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED);
        assertEquals(1, scheduled.size());
        assertEquals("APT-000001", scheduled.get(0).getAppointmentNumber());
    }

    @Test
    @DisplayName("Double booking check - dentist same date and time returns true")
    void testDoubleBookingExists() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(9, 0);

        Appointment existing = new Appointment("APT-000001", patient, dentist, treatment, date, time, null);
        entityManager.persistAndFlush(existing);

        boolean exists = appointmentRepository.existsByDentistIdAndAppointmentDateAndAppointmentTimeAndStatus(
                dentist.getId(), date, time, AppointmentStatus.SCHEDULED);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Double booking check - different time returns false")
    void testNoDoubleBookingDifferentTime() {
        LocalDate date = LocalDate.now().plusDays(1);

        Appointment existing = new Appointment("APT-000001", patient, dentist, treatment, date, LocalTime.of(9, 0), null);
        entityManager.persistAndFlush(existing);

        boolean exists = appointmentRepository.existsByDentistIdAndAppointmentDateAndAppointmentTimeAndStatus(
                dentist.getId(), date, LocalTime.of(10, 0), AppointmentStatus.SCHEDULED);

        assertFalse(exists);
    }

    @Test
    @DisplayName("Search appointments by appointment number")
    void testSearchByAppointmentNumber() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment,
                LocalDate.now().plusDays(1), LocalTime.of(9, 0), null);
        entityManager.persistAndFlush(apt);

        List<Appointment> results = appointmentRepository.searchAppointments("APT-000001");
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Search appointments by patient name")
    void testSearchByPatientName() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment,
                LocalDate.now().plusDays(1), LocalTime.of(9, 0), null);
        entityManager.persistAndFlush(apt);

        List<Appointment> results = appointmentRepository.searchAppointments("Kavindu");
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Search appointments by dentist name")
    void testSearchByDentistName() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment,
                LocalDate.now().plusDays(1), LocalTime.of(9, 0), null);
        entityManager.persistAndFlush(apt);

        List<Appointment> results = appointmentRepository.searchAppointments("Amara");
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Find upcoming appointments")
    void testFindUpcomingAppointments() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment,
                LocalDate.now().plusDays(3), LocalTime.of(9, 0), null);
        entityManager.persistAndFlush(apt);

        List<Appointment> upcoming = appointmentRepository.findByAppointmentDateGreaterThanEqual(LocalDate.now());
        assertFalse(upcoming.isEmpty());
    }
}
