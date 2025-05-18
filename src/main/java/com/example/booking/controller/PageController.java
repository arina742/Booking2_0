package com.example.booking.controller;

import com.example.booking.model.Booking;
import com.example.booking.repositories.BookingRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


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
        String phoneNumber = (String) session.getAttribute("login");
        model.addAttribute("phoneNumber", phoneNumber);
        List<Booking> bookings = bookingRepository.findByPhoneNumber(phoneNumber);
        bookings.sort(Comparator.comparing(Booking::getDate).reversed());
        for (int i = 0; i < bookings.size(); i++) {
            bookings.get(i).setId((long) (i+1));
        }
        model.addAttribute("bookings", bookings);

        return "user";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Получаем текущую сессию
        HttpSession session = request.getSession(false);

        // Если сессия существует - очищаем ее
        if (session != null) {
            session.invalidate(); // Уничтожает сессию
        }

        // Перенаправляем на страницу входа
        return "redirect:/login?logout";
    }

    @GetMapping("/api/bookings")
    public ResponseEntity<List<String>> getBookedHours(@RequestParam String date) {
        LocalDate bookingDate = LocalDate.parse(date);
        List<String> bookedHours = bookingRepository.findByDate(bookingDate)
                .stream()
                .map(Booking::getStartTime)
                .map(time -> time.toString().substring(0, 5))
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookedHours);
    }

}