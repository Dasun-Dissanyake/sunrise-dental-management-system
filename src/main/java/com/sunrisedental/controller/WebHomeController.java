package com.sunrisedental.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller handling user-facing MVC web routes rendered via Thymeleaf.
 */
@Controller
public class WebHomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("appName", "Sunrise Dental Management System");
        model.addAttribute("welcomeMessage", "Welcome to Sunrise Dental Clinic Management Portal");
        model.addAttribute("systemStatus", "Foundation Ready");
        return "index";
    }
}
