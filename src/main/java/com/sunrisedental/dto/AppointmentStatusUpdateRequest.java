package com.sunrisedental.dto;

import com.sunrisedental.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for PATCH status updates.
 */
public class AppointmentStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private AppointmentStatus status;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public AppointmentStatusUpdateRequest() {
    }

    public AppointmentStatusUpdateRequest(AppointmentStatus status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
