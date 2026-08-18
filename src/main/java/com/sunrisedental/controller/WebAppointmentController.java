package com.sunrisedental.controller;

import com.sunrisedental.dto.AppointmentCreateRequest;
import com.sunrisedental.dto.AppointmentResponse;
import com.sunrisedental.dto.AppointmentStatusUpdateRequest;
import com.sunrisedental.dto.AppointmentUpdateRequest;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.model.AppointmentStatus;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.service.DentistService;
import com.sunrisedental.service.PatientService;
import com.sunrisedental.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web MVC Controller serving Thymeleaf views for Appointment management pages.
 */
@Controller
@RequestMapping("/appointments")
public class WebAppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DentistService dentistService;
    private final TreatmentService treatmentService;
    private final com.sunrisedental.service.BillService billService;

    public WebAppointmentController(AppointmentService appointmentService,
                                     PatientService patientService,
                                     DentistService dentistService,
                                     TreatmentService treatmentService,
                                     com.sunrisedental.service.BillService billService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.dentistService = dentistService;
        this.treatmentService = treatmentService;
        this.billService = billService;
    }

    @GetMapping
    public String listAppointments(@RequestParam(required = false) String query,
                                    @RequestParam(required = false) String status,
                                    Model model) {
        if (query != null && !query.isBlank()) {
            model.addAttribute("appointments", appointmentService.searchAppointments(query));
            model.addAttribute("query", query);
        } else if (status != null && !status.isBlank()) {
            try {
                AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
                model.addAttribute("appointments", appointmentService.getAppointmentsByStatus(appointmentStatus));
                model.addAttribute("selectedStatus", status.toUpperCase());
            } catch (IllegalArgumentException e) {
                model.addAttribute("appointments", appointmentService.getAllAppointments());
            }
        } else {
            model.addAttribute("appointments", appointmentService.getAllAppointments());
        }
        model.addAttribute("statuses", AppointmentStatus.values());
        return "appointments";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("appointmentForm", new AppointmentCreateRequest());
        model.addAttribute("patients", patientService.getActivePatients());
        model.addAttribute("dentists", dentistService.getActiveDentists());
        model.addAttribute("treatments", treatmentService.getActiveTreatments());
        model.addAttribute("isEdit", false);
        return "appointment-form";
    }

    @PostMapping("/new")
    public String createAppointment(@Valid @ModelAttribute("appointmentForm") AppointmentCreateRequest request,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientService.getActivePatients());
            model.addAttribute("dentists", dentistService.getActiveDentists());
            model.addAttribute("treatments", treatmentService.getActiveTreatments());
            model.addAttribute("isEdit", false);
            return "appointment-form";
        }

        try {
            AppointmentResponse created = appointmentService.createAppointment(request);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment " + created.getAppointmentNumber() + " created successfully.");
            return "redirect:/appointments/" + created.getId();
        } catch (CustomApiException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("patients", patientService.getActivePatients());
            model.addAttribute("dentists", dentistService.getActiveDentists());
            model.addAttribute("treatments", treatmentService.getActiveTreatments());
            model.addAttribute("isEdit", false);
            return "appointment-form";
        }
    }

    @GetMapping("/{id}")
    public String viewAppointment(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            AppointmentResponse appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            model.addAttribute("statuses", AppointmentStatus.values());

            try {
                com.sunrisedental.dto.BillResponse bill = billService.getBillByAppointmentId(id);
                model.addAttribute("hasBill", true);
                model.addAttribute("billId", bill.getId());
                model.addAttribute("billNumber", bill.getBillNumber());
            } catch (Exception e) {
                model.addAttribute("hasBill", false);
            }

            return "appointment-detail";
        } catch (CustomApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/appointments";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            AppointmentResponse appointment = appointmentService.getAppointmentById(id);

            AppointmentUpdateRequest form = new AppointmentUpdateRequest(
                    appointment.getPatientId(),
                    appointment.getDentistId(),
                    appointment.getTreatmentId(),
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus(),
                    appointment.getNotes()
            );

            model.addAttribute("appointmentForm", form);
            model.addAttribute("appointmentId", id);
            model.addAttribute("appointmentNumber", appointment.getAppointmentNumber());
            model.addAttribute("patients", patientService.getActivePatients());
            model.addAttribute("dentists", dentistService.getActiveDentists());
            model.addAttribute("treatments", treatmentService.getActiveTreatments());
            model.addAttribute("statuses", AppointmentStatus.values());
            model.addAttribute("isEdit", true);
            return "appointment-form";
        } catch (CustomApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/appointments";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateAppointment(@PathVariable Long id,
                                     @Valid @ModelAttribute("appointmentForm") AppointmentUpdateRequest request,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("appointmentId", id);
            model.addAttribute("patients", patientService.getActivePatients());
            model.addAttribute("dentists", dentistService.getActiveDentists());
            model.addAttribute("treatments", treatmentService.getActiveTreatments());
            model.addAttribute("statuses", AppointmentStatus.values());
            model.addAttribute("isEdit", true);
            return "appointment-form";
        }

        try {
            AppointmentResponse updated = appointmentService.updateAppointment(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment " + updated.getAppointmentNumber() + " updated successfully.");
            return "redirect:/appointments/" + id;
        } catch (CustomApiException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("appointmentId", id);
            model.addAttribute("patients", patientService.getActivePatients());
            model.addAttribute("dentists", dentistService.getActiveDentists());
            model.addAttribute("treatments", treatmentService.getActiveTreatments());
            model.addAttribute("statuses", AppointmentStatus.values());
            model.addAttribute("isEdit", true);
            return "appointment-form";
        }
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                                @RequestParam String status,
                                @RequestParam(required = false) String notes,
                                RedirectAttributes redirectAttributes) {
        try {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            AppointmentStatusUpdateRequest request = new AppointmentStatusUpdateRequest(appointmentStatus, notes);
            appointmentService.updateAppointmentStatus(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment status updated to " + status + ".");
        } catch (CustomApiException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/appointments/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled successfully.");
        } catch (CustomApiException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/appointments";
    }
}
