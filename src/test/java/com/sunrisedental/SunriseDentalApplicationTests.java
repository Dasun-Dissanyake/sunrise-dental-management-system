package com.sunrisedental;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring Boot Application Context Smoke Test.
 * Uses JUnit 5 (@Test from org.junit.jupiter.api) and Mockito readiness.
 */
@SpringBootTest
class SunriseDentalApplicationTests {

    @Test
    @DisplayName("Verify Spring Boot application context loads successfully")
    void contextLoads() {
        // Smoke test verifying spring container initialization
        assertTrue(true, "Application context initialized");
    }
}
