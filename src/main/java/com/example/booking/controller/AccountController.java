package com.example.booking.controller;

import com.example.booking.model.Booking;
import com.example.booking.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class AccountController {
//    @Autowired
//    private BookingRepository bookingRepository;
//
//    @GetMapping("/user")
//    public String user(Model model, Principal principal) {
//        // Получаем номер телефона аутентифицированного пользователя
//        String phoneNumber = principal.getName(); // Предполагаем, что username - это телефон
//        model.addAttribute("phoneNumber", phoneNumber);
//
//        // Получаем историю бронирований из БД
//        List<Booking> bookings = bookingRepository.findBookingsByLogin(phoneNumber);
//        model.addAttribute("bookings", bookings);
//        model.addAttribute("phoneNumber", phoneNumber);  // Ключевое имя "phoneNumber"
//
//        return "user";
//    }
}
