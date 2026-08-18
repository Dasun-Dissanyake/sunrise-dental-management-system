package com.sunrisedental.service;

import com.sunrisedental.dto.DentistResponse;

import java.util.List;

/**
 * Service interface for Dentist business operations.
 */
public interface DentistService {

    List<DentistResponse> getAllDentists();

    List<DentistResponse> getActiveDentists();

    DentistResponse getDentistById(Long id);

    DentistResponse getDentistByNumber(String dentistNumber);
}
