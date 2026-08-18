package com.sunrisedental.service;

import com.sunrisedental.dto.TreatmentResponse;

import java.util.List;

/**
 * Service interface for Treatment business operations.
 */
public interface TreatmentService {

    List<TreatmentResponse> getAllTreatments();

    List<TreatmentResponse> getActiveTreatments();

    TreatmentResponse getTreatmentById(Long id);

    TreatmentResponse getTreatmentByCode(String treatmentCode);
}
