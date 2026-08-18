package com.sunrisedental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Sunrise Dental Management System.
 *
 * Sunrise Dental Management System is a distributed dental clinic appointment
 * and patient management application designed using clean layered architecture
 * and Spring Boot REST web services.
 */
@SpringBootApplication
public class SunriseDentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SunriseDentalApplication.class, args);
    }
}
