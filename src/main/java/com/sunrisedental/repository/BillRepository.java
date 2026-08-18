package com.sunrisedental.repository;

import com.sunrisedental.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Bill persistence operations.
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByBillNumber(String billNumber);

    Optional<Bill> findByAppointmentId(Long appointmentId);

    boolean existsByBillNumber(String billNumber);

    boolean existsByAppointmentId(Long appointmentId);

    @Query("SELECT MAX(b.id) FROM Bill b")
    Long findMaxBillId();
}
