package com.sunrisedental.repository;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Appointment persistence operations.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    boolean existsByAppointmentNumber(String appointmentNumber);

    /**
     * Double Booking Prevention Check: Checks if a dentist already has a SCHEDULED appointment at date and time.
     */
    boolean existsByDentistIdAndAppointmentDateAndAppointmentTimeAndStatus(Long dentistId, LocalDate date, LocalTime time, AppointmentStatus status);

    /**
     * Double Booking Prevention Check during update excluding current appointment ID.
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.dentist.id = :dentistId AND a.appointmentDate = :date AND a.appointmentTime = :time AND a.status = :status AND a.id <> :id")
    boolean existsByDentistIdAndAppointmentDateAndAppointmentTimeAndStatusAndIdNot(
            @Param("dentistId") Long dentistId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time,
            @Param("status") AppointmentStatus status,
            @Param("id") Long id);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByAppointmentDateGreaterThanEqual(LocalDate date);

    /**
     * Search appointments by appointment number, patient full name, or dentist full name (case-insensitive).
     */
    @Query("SELECT a FROM Appointment a WHERE " +
           "LOWER(a.appointmentNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.patient.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.dentist.fullName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Appointment> searchAppointments(@Param("query") String query);

    @Query("SELECT MAX(a.id) FROM Appointment a")
    Long findMaxAppointmentId();

    List<Appointment> findByAppointmentDate(LocalDate date);

    List<Appointment> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);
}
