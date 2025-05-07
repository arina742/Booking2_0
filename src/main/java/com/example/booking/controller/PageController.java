package com.example.booking.controller;

import com.example.booking.model.Booking;
import com.example.booking.services.AuthService;
import jakarta.servlet.http.HttpSession;
import com.example.booking.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;


@Controller
public class PageController {
    @Autowired
    private BookingRepository bookingRepository;
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Главная страница");
        return "home";
    }

    @GetMapping("/photo")
    public String photo(Model model) {
        model.addAttribute("pageTitle", "Галерея");
        return "photo";
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

    @GetMapping("/user")
    public String user(Model model, HttpSession session) {
        // Получаем логин из сессии
        String login = (String) session.getAttribute("userLogin");

        if (login == null) {
            return "redirect:/login"; // Перенаправляем если не авторизован
        }

        try {
            login = AuthService.getFormatNumber(login);
            List<Booking> bookings = bookingRepository.findBookingsByLogin(login);
            model.addAttribute("bookings", bookings);
            model.addAttribute("phoneNumber", login); // Используем login как phoneNumber
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке данных");
            // Логирование ошибки
            System.err.println("Ошибка при загрузке бронирований: " + e.getMessage());
            e.printStackTrace();
        }

        return "user";
    }
}