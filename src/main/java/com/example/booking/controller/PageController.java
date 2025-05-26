package com.example.booking.controller;

import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;
import com.example.booking.repositories.BookingRepository;
import com.example.booking.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;



@Controller
public class PageController {
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingRepository bookingRepository;
    private UserRepository userRepository;

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
    public String booking(Model model, HttpSession session) {
        bookingRepository.updateExpiredBookings();
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
        bookingRepository.updateExpiredBookings();
        String phoneNumber = (String) session.getAttribute("login");
        model.addAttribute("phoneNumber", phoneNumber);
        List<Booking> bookings = bookingRepository.findByPhoneNumber(phoneNumber);
        List<Booking> sortedBookings = bookings.stream()
                .sorted(Comparator
                        .comparing(Booking::getStatus, Comparator.comparing(status -> {
                            // Порядок сортировки статусов
                            if (status == BookingStatus.ACTIVE) return 1;
                            if (status == BookingStatus.COMPLETED) return 2;
                            return 3;
                        }))
                        .thenComparing(Booking::getDate)
                        .thenComparing(Booking::getStartTime)

                )
                .collect(Collectors.toList());

        model.addAttribute("bookings", sortedBookings);
        return "user";
    }

    @GetMapping("/admin")
    public String admin(Model model, HttpSession session) {
        bookingRepository.updateExpiredBookings();
        String phoneNumber = (String) session.getAttribute("login");
        model.addAttribute("phoneNumber", phoneNumber);
        List<Booking> bookings = bookingRepository.findAll();
        List<Booking> sortedBookings = bookings.stream()
                .sorted(Comparator
                        .comparing(Booking::getStatus, Comparator.comparing(status -> {
                            if (status == BookingStatus.ACTIVE) return 1;
                            if (status == BookingStatus.COMPLETED) return 2;
                            return 3;
                        }))
                        .thenComparing(Booking::getDate)
                        .thenComparing(Booking::getStartTime)
                )
                .collect(Collectors.toList());

        model.addAttribute("bookings", sortedBookings);
        return "admin";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate(); // Уничтожает сессию
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/api/bookings")
    public ResponseEntity<List<String>> getBookedHours(@RequestParam String date) {
        bookingRepository.updateExpiredBookings();
        LocalDate bookingDate = LocalDate.parse(date);
        List<String> bookedHours = bookingRepository.findByDate(bookingDate)
                .stream()
                .map(Booking::getStartTime)
                .map(time -> time.toString().substring(0, 5))
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookedHours);
    }

    @GetMapping("/{id}/cancel")
    @Transactional
    public String cancelBooking(@PathVariable("id") Long id) {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        logger.info("Начало обработки отмены бронирования ID: {}", id);

        try {
            bookingRepository.cancelBooking(id);
            logger.info("Бронирование ID: {} успешно отменено", id);
        } catch (Exception e) {
            logger.error("Ошибка при отмене бронирования ID: {}", id, e);
        }

        return "redirect:/user";
    }

}