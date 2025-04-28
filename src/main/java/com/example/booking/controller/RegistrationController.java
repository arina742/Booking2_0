package com.example.booking.controller;

import com.example.booking.repositories.UserRepository;
import com.example.booking.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registration")
    public String registration(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String password2,
            Model model)
    {
        if(!password.equals(password2)){
            model.addAttribute("error", "Пароли не совпадают");
            return "registration";
        }
        boolean isRegistered = authService.register(username, password);

        if (!isRegistered) {
            model.addAttribute("error", "Пользователь с таким именем уже существует!");
           return "registration";
        }

        return  "redirect:/login"; // Перенаправляем на страницу входа
    }
}
