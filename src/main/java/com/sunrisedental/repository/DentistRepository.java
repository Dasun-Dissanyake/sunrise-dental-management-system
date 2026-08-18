package com.sunrisedental.repository;

import com.sunrisedental.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Dentist persistence.
 */
@Repository
public interface DentistRepository extends JpaRepository<Dentist, Long> {

    Optional<Dentist> findByDentistNumber(String dentistNumber);

    boolean existsByDentistNumber(String dentistNumber);

    List<Dentist> findByActiveTrue();

    @Query("SELECT MAX(d.id) FROM Dentist d")
    Long findMaxDentistId();
}
