package com.example.booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class PageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Главная страница");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "О нас");
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Контакты");
        return "contact";
    }

    @GetMapping("/booking")
    public String booking(Model model) {
        model.addAttribute("pageTitle", "Бронирование");
        return "booking";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Вход");
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("pageTitle", "Регистрация");
        return "registration";
    }
}