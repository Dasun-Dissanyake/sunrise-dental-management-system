package com.sunrisedental.service;

import com.sunrisedental.dto.DentistResponse;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.repository.DentistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Dentist business operations.
 */
@Service
@Transactional
public class DentistServiceImpl implements DentistService {

    private final DentistRepository dentistRepository;

    public DentistServiceImpl(DentistRepository dentistRepository) {
        this.dentistRepository = dentistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DentistResponse> getAllDentists() {
        return dentistRepository.findAll().stream()
                .map(DentistResponse::fromDentist)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DentistResponse> getActiveDentists() {
        return dentistRepository.findByActiveTrue().stream()
                .map(DentistResponse::fromDentist)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DentistResponse getDentistById(Long id) {
        return dentistRepository.findById(id)
                .map(DentistResponse::fromDentist)
                .orElseThrow(() -> new CustomApiException("Dentist not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public DentistResponse getDentistByNumber(String dentistNumber) {
        return dentistRepository.findByDentistNumber(dentistNumber)
                .map(DentistResponse::fromDentist)
                .orElseThrow(() -> new CustomApiException("Dentist not found with number: " + dentistNumber));
    }
}
