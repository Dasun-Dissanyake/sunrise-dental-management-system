package com.sunrisedental.repository;

import com.sunrisedental.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Treatment persistence.
 */
@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    Optional<Treatment> findByTreatmentCode(String treatmentCode);

    boolean existsByTreatmentCode(String treatmentCode);

    List<Treatment> findByActiveTrue();
}
