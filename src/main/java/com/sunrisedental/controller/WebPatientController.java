package com.sunrisedental.controller;

import com.sunrisedental.dto.PatientCreateRequest;
import com.sunrisedental.dto.PatientResponse;
import com.sunrisedental.dto.PatientUpdateRequest;
import com.sunrisedental.service.PatientService;
import com.sunrisedental.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller handling user-facing MVC web routes for Patient Management rendered via Thymeleaf.
 */
@Controller
@RequestMapping("/patients")
public class WebPatientController {

    private final PatientService patientService;
    private final UserService userService;

    public WebPatientController(PatientService patientService, UserService userService) {
        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping
    public String listPatients(@RequestParam(value = "query", required = false) String query, Model model) {
        List<PatientResponse> patients = (query != null && !query.isBlank())
                ? patientService.searchPatients(query)
                : patientService.getAllPatients();

        model.addAttribute("patients", patients);
        model.addAttribute("query", query != null ? query : "");
        model.addAttribute("user", userService.getCurrentAuthenticatedUser());
        return "patients";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("patientRequest", new PatientCreateRequest());
        model.addAttribute("user", userService.getCurrentAuthenticatedUser());
        model.addAttribute("isEdit", false);
        return "patient-form";
    }

    @PostMapping("/new")
    public String createPatient(@Valid @ModelAttribute("patientRequest") PatientCreateRequest request,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userService.getCurrentAuthenticatedUser());
            model.addAttribute("isEdit", false);
            return "patient-form";
        }

        PatientResponse response = patientService.registerPatient(request);
        redirectAttributes.addFlashAttribute("successMessage",
                "Patient registered successfully. Patient Number: " + response.getPatientNumber());
        return "redirect:/patients";
    }

    @GetMapping("/{id}")
    public String viewPatient(@PathVariable Long id, Model model) {
        PatientResponse patient = patientService.getPatientById(id);
        model.addAttribute("patient", patient);
        model.addAttribute("user", userService.getCurrentAuthenticatedUser());
        return "patient-detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        PatientResponse patient = patientService.getPatientById(id);
        PatientUpdateRequest updateRequest = new PatientUpdateRequest(
                patient.getFullName(),
                patient.getAddress(),
                patient.getContactNumber(),
                patient.getEmail(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.isActive()
        );

        model.addAttribute("patientRequest", updateRequest);
        model.addAttribute("patientId", id);
        model.addAttribute("patientNumber", patient.getPatientNumber());
        model.addAttribute("user", userService.getCurrentAuthenticatedUser());
        model.addAttribute("isEdit", true);
        return "patient-form";
    }

    @PostMapping("/{id}/edit")
    public String updatePatient(@PathVariable Long id,
                                @Valid @ModelAttribute("patientRequest") PatientUpdateRequest request,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            PatientResponse patient = patientService.getPatientById(id);
            model.addAttribute("patientId", id);
            model.addAttribute("patientNumber", patient.getPatientNumber());
            model.addAttribute("user", userService.getCurrentAuthenticatedUser());
            model.addAttribute("isEdit", true);
            return "patient-form";
        }

        PatientResponse updated = patientService.updatePatient(id, request);
        redirectAttributes.addFlashAttribute("successMessage",
                "Patient profile updated successfully for " + updated.getPatientNumber());
        return "redirect:/patients/" + id;
    }

    @PostMapping("/{id}/deactivate")
    public String deactivatePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        PatientResponse deactivated = patientService.deactivatePatient(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Patient " + deactivated.getPatientNumber() + " has been deactivated successfully.");
        return "redirect:/patients";
    }
}
