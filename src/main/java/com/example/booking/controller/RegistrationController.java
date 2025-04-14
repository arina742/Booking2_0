package com.example.booking.controller;

import com.example.booking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

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

        boolean isRegistered = userRepository.existsByLogin(username);
       if (!isRegistered) {
            model.addAttribute("error", "Пользователь с таким именем уже существует!");
            return "registration";
        }

        return "redirect:/login"; // Перенаправляем на страницу входа
    }
}
