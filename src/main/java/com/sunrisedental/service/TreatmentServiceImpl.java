package com.sunrisedental.service;

import com.sunrisedental.dto.TreatmentResponse;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.repository.TreatmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Treatment business operations.
 */
@Service
@Transactional
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;

    public TreatmentServiceImpl(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponse> getAllTreatments() {
        return treatmentRepository.findAll().stream()
                .map(TreatmentResponse::fromTreatment)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponse> getActiveTreatments() {
        return treatmentRepository.findByActiveTrue().stream()
                .map(TreatmentResponse::fromTreatment)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentResponse getTreatmentById(Long id) {
        return treatmentRepository.findById(id)
                .map(TreatmentResponse::fromTreatment)
                .orElseThrow(() -> new CustomApiException("Treatment not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentResponse getTreatmentByCode(String treatmentCode) {
        return treatmentRepository.findByTreatmentCode(treatmentCode)
                .map(TreatmentResponse::fromTreatment)
                .orElseThrow(() -> new CustomApiException("Treatment not found with code: " + treatmentCode));
    }
}
