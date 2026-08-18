package com.sunrisedental.dto;

/**
 * DTO for dentist performance report entries.
 */
public class DentistReportResponse {

    private String dentistName;
    private long totalAppointments;
    private long completed;
    private long cancelled;
    private long scheduled;
    private long noShow;

    public DentistReportResponse() {}

    public DentistReportResponse(String dentistName, long totalAppointments, long completed, long cancelled, long scheduled, long noShow) {
        this.dentistName = dentistName;
        this.totalAppointments = totalAppointments;
        this.completed = completed;
        this.cancelled = cancelled;
        this.scheduled = scheduled;
        this.noShow = noShow;
    }

    // Getters and Setters
    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }
    public long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(long totalAppointments) { this.totalAppointments = totalAppointments; }
    public long getCompleted() { return completed; }
    public void setCompleted(long completed) { this.completed = completed; }
    public long getCancelled() { return cancelled; }
    public void setCancelled(long cancelled) { this.cancelled = cancelled; }
    public long getScheduled() { return scheduled; }
    public void setScheduled(long scheduled) { this.scheduled = scheduled; }
    public long getNoShow() { return noShow; }
    public void setNoShow(long noShow) { this.noShow = noShow; }
}
