package com.sunrisedental.service;

import com.sunrisedental.dto.BillResponse;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.model.*;
import com.sunrisedental.repository.AppointmentRepository;
import com.sunrisedental.repository.BillRepository;
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
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private BillServiceImpl billService;

    private Patient patient;
    private Dentist dentist;
    private Treatment checkupTreatment;
    private Treatment rootCanalTreatment;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        patient = new Patient("PAT-000001", "Kavindu Perera", "Colombo", "0771234567", null, LocalDate.of(1995, 5, 12), "Male");
        patient.setId(1L);

        dentist = new Dentist("DENT-000001", "Dr. Amara Perera", "General Dentistry", "0771234567");
        dentist.setId(1L);

        checkupTreatment = new Treatment("TRT-001", "Routine Checkup", "Standard checkup", new BigDecimal("500.00"), new BigDecimal("200.00"));
        checkupTreatment.setId(1L);

        rootCanalTreatment = new Treatment("TRT-003", "Root Canal", "Endodontic treatment", new BigDecimal("8000.00"), new BigDecimal("500.00"));
        rootCanalTreatment.setId(2L);

        appointment = new Appointment("APT-000001", patient, dentist, checkupTreatment, LocalDate.now().plusDays(1), LocalTime.of(9, 0), "Notes");
        appointment.setId(1L);
    }

    @Test
    @DisplayName("Test 1: Calculate total for 500 treatment cost + 200 consultation fee = 700 total")
    void testCalculateTotal_500Plus200Equals700() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(billRepository.existsByAppointmentId(1L)).thenReturn(false);
        when(billRepository.findMaxBillId()).thenReturn(null);
        when(billRepository.existsByBillNumber("REC-000001")).thenReturn(false);

        Bill savedBill = new Bill("REC-000001", appointment, new BigDecimal("500.00"), new BigDecimal("200.00"), new BigDecimal("700.00"), LocalDateTime.now());
        savedBill.setId(1L);
        when(billRepository.save(any(Bill.class))).thenReturn(savedBill);

        BillResponse response = billService.generateBill(1L);

        assertNotNull(response);
        assertEquals(new BigDecimal("500.00"), response.getTreatmentCost());
        assertEquals(new BigDecimal("200.00"), response.getConsultationFee());
        assertEquals(new BigDecimal("700.00"), response.getTotalAmount());
        assertEquals("REC-000001", response.getBillNumber());
    }

    @Test
    @DisplayName("Test 2: Calculate total for 8000 treatment cost + 500 consultation fee = 8500 total")
    void testCalculateTotal_8000Plus500Equals8500() {
        Appointment rootCanalApt = new Appointment("APT-000002", patient, dentist, rootCanalTreatment, LocalDate.now().plusDays(2), LocalTime.of(10, 0), null);
        rootCanalApt.setId(2L);

        when(appointmentRepository.findById(2L)).thenReturn(Optional.of(rootCanalApt));
        when(billRepository.existsByAppointmentId(2L)).thenReturn(false);
        when(billRepository.findMaxBillId()).thenReturn(1L);
        when(billRepository.existsByBillNumber("REC-000002")).thenReturn(false);

        Bill savedBill = new Bill("REC-000002", rootCanalApt, new BigDecimal("8000.00"), new BigDecimal("500.00"), new BigDecimal("8500.00"), LocalDateTime.now());
        savedBill.setId(2L);
        when(billRepository.save(any(Bill.class))).thenReturn(savedBill);

        BillResponse response = billService.generateBill(2L);

        assertNotNull(response);
        assertEquals(new BigDecimal("8000.00"), response.getTreatmentCost());
        assertEquals(new BigDecimal("500.00"), response.getConsultationFee());
        assertEquals(new BigDecimal("8500.00"), response.getTotalAmount());
        assertEquals("REC-000002", response.getBillNumber());
    }

    @Test
    @DisplayName("Test 3: Bill generation for a valid appointment succeeds")
    void testGenerateBill_ValidAppointment() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(billRepository.existsByAppointmentId(1L)).thenReturn(false);
        when(billRepository.findMaxBillId()).thenReturn(null);
        when(billRepository.existsByBillNumber("REC-000001")).thenReturn(false);

        Bill savedBill = new Bill("REC-000001", appointment, new BigDecimal("500.00"), new BigDecimal("200.00"), new BigDecimal("700.00"), LocalDateTime.now());
        savedBill.setId(1L);
        when(billRepository.save(any(Bill.class))).thenReturn(savedBill);

        BillResponse response = billService.generateBill(1L);

        assertNotNull(response);
        assertEquals("PAT-000001", response.getPatientNumber());
        assertEquals("Kavindu Perera", response.getPatientName());
        assertEquals("Dr. Amara Perera", response.getDentistName());
        assertEquals("Routine Checkup", response.getTreatmentName());
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    @Test
    @DisplayName("Test 4: Bill generation for a nonexistent appointment fails appropriately")
    void testGenerateBill_NonexistentAppointment() {
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        CustomApiException exception = assertThrows(CustomApiException.class, () -> billService.generateBill(999L));
        assertTrue(exception.getMessage().contains("Appointment not found"));
        verify(billRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 5: Duplicate bill generation for the same appointment is prevented")
    void testGenerateBill_DuplicateBillPrevented() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(billRepository.existsByAppointmentId(1L)).thenReturn(true);

        CustomApiException exception = assertThrows(CustomApiException.class, () -> billService.generateBill(1L));
        assertTrue(exception.getMessage().contains("already been generated"));
        verify(billRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 6: Negative/invalid billing values are rejected")
    void testGenerateBill_NegativeTreatmentCostRejected() {
        Treatment invalidTreatment = new Treatment("TRT-ERR", "Invalid", "Desc", new BigDecimal("-100.00"), new BigDecimal("200.00"));
        Appointment invalidApt = new Appointment("APT-000003", patient, dentist, invalidTreatment, LocalDate.now(), LocalTime.of(11, 0), null);
        invalidApt.setId(3L);

        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(invalidApt));
        when(billRepository.existsByAppointmentId(3L)).thenReturn(false);

        CustomApiException exception = assertThrows(CustomApiException.class, () -> billService.generateBill(3L));
        assertTrue(exception.getMessage().contains("Invalid treatment cost"));
        verify(billRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get bill by ID returns bill response")
    void testGetBillById() {
        Bill bill = new Bill("REC-000001", appointment, new BigDecimal("500.00"), new BigDecimal("200.00"), new BigDecimal("700.00"), LocalDateTime.now());
        bill.setId(1L);
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));

        BillResponse response = billService.getBillById(1L);

        assertNotNull(response);
        assertEquals("REC-000001", response.getBillNumber());
    }

    @Test
    @DisplayName("Get bill by appointment ID returns bill response")
    void testGetBillByAppointmentId() {
        Bill bill = new Bill("REC-000001", appointment, new BigDecimal("500.00"), new BigDecimal("200.00"), new BigDecimal("700.00"), LocalDateTime.now());
        bill.setId(1L);
        when(billRepository.findByAppointmentId(1L)).thenReturn(Optional.of(bill));

        BillResponse response = billService.getBillByAppointmentId(1L);

        assertNotNull(response);
        assertEquals("REC-000001", response.getBillNumber());
    }

    @Test
    @DisplayName("Get all bills returns list of bills")
    void testGetAllBills() {
        Bill bill = new Bill("REC-000001", appointment, new BigDecimal("500.00"), new BigDecimal("200.00"), new BigDecimal("700.00"), LocalDateTime.now());
        when(billRepository.findAll()).thenReturn(List.of(bill));

        List<BillResponse> bills = billService.getAllBills();

        assertEquals(1, bills.size());
        assertEquals("REC-000001", bills.get(0).getBillNumber());
    }
}
