package com.sunrisedental.controller;

import com.sunrisedental.dto.TreatmentResponse;
import com.sunrisedental.service.TreatmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Web MVC Controller serving Thymeleaf views for Treatment catalog pages.
 */
@Controller
@RequestMapping("/treatments")
public class WebTreatmentController {

    private final TreatmentService treatmentService;

    public WebTreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @GetMapping
    public String listTreatments(Model model) {
        List<TreatmentResponse> treatments = treatmentService.getActiveTreatments();
        model.addAttribute("treatments", treatments);
        return "treatments";
    }
}
