package com.sunrisedental.controller;

import com.sunrisedental.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller serving application health and status APIs.
 * Demonstrates backend REST API capabilities for distributed architecture.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealthStatus() {
        Map<String, Object> statusDetails = new HashMap<>();
        statusDetails.put("service", "Sunrise Dental Management System REST API");
        statusDetails.put("status", "UP");
        statusDetails.put("architecture", "Distributed Web Services Layer");
        statusDetails.put("version", "1.0.0");

        return ResponseEntity.ok(ApiResponse.success("System operational", statusDetails));
    }
}
