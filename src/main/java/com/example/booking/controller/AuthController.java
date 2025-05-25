package com.example.booking.controller;

import com.example.booking.model.UserCredentials;
import com.example.booking.services.AuthService;
import jakarta.servlet.http.HttpSession;
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
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false) boolean isAdmin,
                        HttpSession session) {

        if (authService.authenticate(username, password, isAdmin)) {
            String formattedPhone = authService.getFormatNumber(username);
            session.setAttribute("login", formattedPhone);
            session.setAttribute("isAdmin", isAdmin);

            return isAdmin ? "redirect:/admin" : "redirect:/user";
        }

        return "redirect:/login?error";
    }
}