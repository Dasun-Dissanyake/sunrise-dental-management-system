package com.sunrisedental.service;

import com.sunrisedental.model.Dentist;
import com.sunrisedental.model.Treatment;
import com.sunrisedental.repository.DentistRepository;
import com.sunrisedental.repository.TreatmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Startup initializer that seeds sample Dentists and Treatments into the database
 * if they do not already exist. Runs after AdminUserInitializer (Order 2).
 */
@Component
@Order(2)
public class ClinicDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ClinicDataInitializer.class);

    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;

    public ClinicDataInitializer(DentistRepository dentistRepository,
                                  TreatmentRepository treatmentRepository) {
        this.dentistRepository = dentistRepository;
        this.treatmentRepository = treatmentRepository;
    }

    @Override
    public void run(String... args) {
        seedDentists();
        seedTreatments();
    }

    private void seedDentists() {
        if (dentistRepository.count() > 0) {
            log.info("Dentists already seeded. Skipping.");
            return;
        }

        dentistRepository.save(new Dentist("DENT-000001", "Dr. Amara Perera", "General Dentistry", "0771234567"));
        dentistRepository.save(new Dentist("DENT-000002", "Dr. Roshan Silva", "Orthodontics", "0779876543"));
        dentistRepository.save(new Dentist("DENT-000003", "Dr. Nilufar Fernando", "Endodontics", "0765432100"));

        log.info("Seeded 3 sample dentists.");
    }

    private void seedTreatments() {
        if (treatmentRepository.count() > 0) {
            log.info("Treatments already seeded. Skipping.");
            return;
        }

        treatmentRepository.save(new Treatment("TRT-001", "Routine Checkup", "Standard dental checkup and cleaning", new BigDecimal("500.00"), new BigDecimal("200.00")));
        treatmentRepository.save(new Treatment("TRT-002", "Tooth Extraction", "Removal of a damaged or decayed tooth", new BigDecimal("2500.00"), new BigDecimal("300.00")));
        treatmentRepository.save(new Treatment("TRT-003", "Root Canal", "Endodontic root canal treatment", new BigDecimal("8000.00"), new BigDecimal("500.00")));
        treatmentRepository.save(new Treatment("TRT-004", "Dental Filling", "Composite resin filling for cavities", new BigDecimal("3000.00"), new BigDecimal("300.00")));
        treatmentRepository.save(new Treatment("TRT-005", "Teeth Whitening", "Professional teeth whitening procedure", new BigDecimal("6000.00"), new BigDecimal("400.00")));

        log.info("Seeded 5 sample treatments.");
    }
}
