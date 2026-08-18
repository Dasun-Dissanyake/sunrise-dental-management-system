package com.sunrisedental.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller handling user authentication views.
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
