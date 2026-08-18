package com.sunrisedental.controller;

import com.sunrisedental.dto.UserResponse;
import com.sunrisedental.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller handling the main clinic dashboard view after successful authentication.
 */
@Controller
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        UserResponse currentUser = userService.getCurrentAuthenticatedUser();
        model.addAttribute("user", currentUser);
        model.addAttribute("appName", "Sunrise Dental Management System");
        return "dashboard";
    }
}
