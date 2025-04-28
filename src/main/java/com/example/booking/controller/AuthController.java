package com.example.booking.controller;

import com.example.booking.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    public AuthService authService;

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,@RequestParam String password) {
        if(authService.authenticate(username, password)) {
            return "redirect:/user";
        } else {
            return "redirect:/login?error";
        }
    }
}
