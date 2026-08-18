package com.sunrisedental.service;

import com.sunrisedental.dto.AppointmentCreateRequest;
import com.sunrisedental.dto.AppointmentResponse;
import com.sunrisedental.dto.AppointmentStatusUpdateRequest;
import com.sunrisedental.dto.AppointmentUpdateRequest;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.model.*;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.DentistRepository;
import com.sunrisedental.repository.PatientRepository;
import com.sunrisedental.repository.TreatmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DentistRepository dentistRepository;
    @Mock
    private TreatmentRepository treatmentRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;
    private LocalDate futureDate;
    private LocalTime slotTime;

    @BeforeEach
    void setUp() {
        patient = new Patient("PAT-000001", "Kavindu Perera", "Colombo", "0771234567", null, LocalDate.of(1995, 5, 12), "Male");
        patient.setId(1L);
        patient.setActive(true);

        dentist = new Dentist("DENT-000001", "Dr. Amara Perera", "General Dentistry", "0771234567");
        dentist.setId(1L);

        treatment = new Treatment("TRT-001", "Routine Checkup", "Standard checkup", new BigDecimal("500.00"), new BigDecimal("200.00"));
        treatment.setId(1L);

        futureDate = LocalDate.now().plusDays(2);
        slotTime = LocalTime.of(9, 0);
    }

    @Test
    @DisplayName("Create appointment - success")
    void testCreateAppointment_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentistIdAndAppointmentDateAndAppointmentTimeAndStatus(
                anyLong(), any(), any(), any())).thenReturn(false);
        when(appointmentRepository.findMaxAppointmentId()).thenReturn(null);
        when(appointmentRepository.existsByAppointmentNumber("APT-000001")).thenReturn(false);

        Appointment savedApt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        savedApt.setId(1L);
        savedApt.setCreatedAt(LocalDateTime.now());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedApt);

        AppointmentCreateRequest request = new AppointmentCreateRequest(1L, 1L, 1L, futureDate, slotTime, null);
        AppointmentResponse response = appointmentService.createAppointment(request);

        assertNotNull(response);
        assertEquals("APT-000001", response.getAppointmentNumber());
        assertEquals("Kavindu Perera", response.getPatientName());
        assertEquals("Dr. Amara Perera", response.getDentistName());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Create appointment - sequential number generation APT-000002")
    void testCreateAppointment_NumberGeneration() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentistIdAndAppointmentDateAndAppointmentTimeAndStatus(
                anyLong(), any(), any(), any())).thenReturn(false);
        when(appointmentRepository.findMaxAppointmentId()).thenReturn(1L);
        when(appointmentRepository.existsByAppointmentNumber("APT-000002")).thenReturn(false);

        Appointment savedApt = new Appointment("APT-000002", patient, dentist, treatment, futureDate, slotTime, null);
        savedApt.setId(2L);
        savedApt.setCreatedAt(LocalDateTime.now());
        when(appointmentRepository.save(any())).thenReturn(savedApt);

        AppointmentCreateRequest request = new AppointmentCreateRequest(1L, 1L, 1L, futureDate, slotTime, null);
        AppointmentResponse response = appointmentService.createAppointment(request);

        assertEquals("APT-000002", response.getAppointmentNumber());
    }

    @Test
    @DisplayName("Create appointment - double booking prevention throws exception")
    void testCreateAppointment_DoubleBooking() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentistIdAndAppointmentDateAndAppointmentTimeAndStatus(
                anyLong(), any(), any(), any())).thenReturn(true);

        AppointmentCreateRequest request = new AppointmentCreateRequest(1L, 1L, 1L, futureDate, slotTime, null);

        CustomApiException ex = assertThrows(CustomApiException.class,
                () -> appointmentService.createAppointment(request));

        assertTrue(ex.getMessage().contains("Dr. Amara Perera"));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create appointment - invalid patient throws exception")
    void testCreateAppointment_InvalidPatient() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        AppointmentCreateRequest request = new AppointmentCreateRequest(99L, 1L, 1L, futureDate, slotTime, null);

        assertThrows(CustomApiException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    @DisplayName("Create appointment - invalid dentist throws exception")
    void testCreateAppointment_InvalidDentist() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(99L)).thenReturn(Optional.empty());

        AppointmentCreateRequest request = new AppointmentCreateRequest(1L, 99L, 1L, futureDate, slotTime, null);

        assertThrows(CustomApiException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    @DisplayName("Create appointment - invalid treatment throws exception")
    void testCreateAppointment_InvalidTreatment() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentRepository.findById(99L)).thenReturn(Optional.empty());

        AppointmentCreateRequest request = new AppointmentCreateRequest(1L, 1L, 99L, futureDate, slotTime, null);

        assertThrows(CustomApiException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    @DisplayName("Create appointment - inactive dentist throws exception")
    void testCreateAppointment_InactiveDentist() {
        dentist.setActive(false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));

        AppointmentCreateRequest request = new AppointmentCreateRequest(1L, 1L, 1L, futureDate, slotTime, null);

        assertThrows(CustomApiException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    @DisplayName("Create appointment - inactive treatment throws exception")
    void testCreateAppointment_InactiveTreatment() {
        treatment.setActive(false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));

        AppointmentCreateRequest request = new AppointmentCreateRequest(1L, 1L, 1L, futureDate, slotTime, null);

        assertThrows(CustomApiException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    @DisplayName("Get appointment by ID - found")
    void testGetAppointmentById() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        apt.setId(1L);
        apt.setCreatedAt(LocalDateTime.now());
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(apt));

        AppointmentResponse response = appointmentService.getAppointmentById(1L);

        assertEquals("APT-000001", response.getAppointmentNumber());
    }

    @Test
    @DisplayName("Get appointment by ID - not found throws exception")
    void testGetAppointmentById_NotFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CustomApiException.class, () -> appointmentService.getAppointmentById(99L));
    }

    @Test
    @DisplayName("Get appointment by appointment number - found")
    void testGetAppointmentByNumber() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        apt.setId(1L);
        apt.setCreatedAt(LocalDateTime.now());
        when(appointmentRepository.findByAppointmentNumber("APT-000001")).thenReturn(Optional.of(apt));

        AppointmentResponse response = appointmentService.getAppointmentByNumber("APT-000001");

        assertEquals("APT-000001", response.getAppointmentNumber());
    }

    @Test
    @DisplayName("Get all appointments returns list")
    void testGetAllAppointments() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        apt.setId(1L);
        apt.setCreatedAt(LocalDateTime.now());
        when(appointmentRepository.findAll()).thenReturn(List.of(apt));

        List<AppointmentResponse> list = appointmentService.getAllAppointments();

        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("Search appointments by query returns matching list")
    void testSearchAppointments() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        apt.setId(1L);
        apt.setCreatedAt(LocalDateTime.now());
        when(appointmentRepository.searchAppointments("Kavindu")).thenReturn(List.of(apt));

        List<AppointmentResponse> results = appointmentService.searchAppointments("Kavindu");

        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Cancel appointment sets status to CANCELLED")
    void testCancelAppointment() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        apt.setId(1L);
        apt.setCreatedAt(LocalDateTime.now());
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(apt));
        when(appointmentRepository.save(any())).thenReturn(apt);

        appointmentService.cancelAppointment(1L);

        assertEquals(AppointmentStatus.CANCELLED, apt.getStatus());
        verify(appointmentRepository, times(1)).save(apt);
    }

    @Test
    @DisplayName("Update appointment status - to COMPLETED")
    void testUpdateAppointmentStatus() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        apt.setId(1L);
        apt.setCreatedAt(LocalDateTime.now());
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(apt));
        when(appointmentRepository.save(any())).thenReturn(apt);

        AppointmentStatusUpdateRequest req = new AppointmentStatusUpdateRequest(AppointmentStatus.COMPLETED, "Completed successfully");
        AppointmentResponse response = appointmentService.updateAppointmentStatus(1L, req);

        assertEquals(AppointmentStatus.COMPLETED, apt.getStatus());
    }

    @Test
    @DisplayName("Update appointment - double booking on update throws exception")
    void testUpdateAppointment_DoubleBookingOnEdit() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        apt.setId(1L);
        apt.setCreatedAt(LocalDateTime.now());
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(apt));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentistIdAndAppointmentDateAndAppointmentTimeAndStatusAndIdNot(
                anyLong(), any(), any(), any(), anyLong())).thenReturn(true);

        AppointmentUpdateRequest request = new AppointmentUpdateRequest(1L, 1L, 1L, futureDate, slotTime, AppointmentStatus.SCHEDULED, null);

        assertThrows(CustomApiException.class, () -> appointmentService.updateAppointment(1L, request));
    }

    @Test
    @DisplayName("Patient JPA relationship is set on appointment")
    void testPatientRelationship() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        assertEquals("PAT-000001", apt.getPatient().getPatientNumber());
        assertEquals("Kavindu Perera", apt.getPatient().getFullName());
    }

    @Test
    @DisplayName("Dentist JPA relationship is set on appointment")
    void testDentistRelationship() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        assertEquals("DENT-000001", apt.getDentist().getDentistNumber());
        assertEquals("Dr. Amara Perera", apt.getDentist().getFullName());
    }

    @Test
    @DisplayName("Treatment JPA relationship is set on appointment")
    void testTreatmentRelationship() {
        Appointment apt = new Appointment("APT-000001", patient, dentist, treatment, futureDate, slotTime, null);
        assertEquals("TRT-001", apt.getTreatment().getTreatmentCode());
        assertEquals("Routine Checkup", apt.getTreatment().getTreatmentName());
    }
}
