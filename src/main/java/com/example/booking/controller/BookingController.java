package com.example.booking.controller;

import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;
import com.example.booking.repositories.BookingRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingRepository bookingRepository;

    // Получение занятых слотов с учетом количества мест
    @GetMapping("/slots")
    public ResponseEntity<?> getBookedSlots(
            @RequestParam String date,
            @RequestParam(required = false) String placeType) {

        try {
            LocalDate bookingDate = LocalDate.parse(date);
            List<Booking> bookings;

            if (placeType != null) {
                bookings = bookingRepository.findByDateAndPlaceType(bookingDate, placeType);
            } else {
                bookings = bookingRepository.findByDate(bookingDate);
            }

            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    // Проверка доступности перед бронированием
    @PostMapping("/check")
    public ResponseEntity<?> checkAvailability(
            @RequestBody BookingCheckRequest request,
            HttpSession session) {

        String phoneNumber = (String) session.getAttribute("login");
        if (phoneNumber == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            LocalDate date = LocalDate.parse(request.getDate());
            LocalTime startTime = LocalTime.parse(request.getStartTime());
            LocalTime endTime = LocalTime.parse(request.getEndTime());

            int maxPlaces = "переговорная".equals(request.getPlaceType()) ? 3 : 25;

            List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                    date, startTime, endTime, request.getPlaceType());

            if (overlapping.size() >= maxPlaces) {
                return ResponseEntity.ok(Map.of(
                        "available", false,
                        "message", "Все места этого типа уже заняты"
                ));
            }

            return ResponseEntity.ok(Map.of("available", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    // Создание брони с транзакцией
    @Transactional
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody BookingRequest request,
            HttpSession session) {

        String phoneNumber = (String) session.getAttribute("login");
        if (phoneNumber == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // Повторная проверка в транзакции
            int maxPlaces = "переговорная".equals(request.getPlaceType()) ? 3 : 25;
            List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                    request.getDate(), request.getStartTime(), request.getEndTime(),
                    request.getPlaceType());

            if (overlapping.size() >= maxPlaces) {
                return ResponseEntity.badRequest().body("Все места уже заняты");
            }

            // Создание брони
            Booking booking = new Booking();
            booking.setDate(request.getDate());
            booking.setStartTime(request.getStartTime());
            booking.setEndTime(request.getEndTime());
            booking.setPlaceType(request.getPlaceType());
            booking.setPhoneNumber(phoneNumber);
            booking.setStatus(BookingStatus.ACTIVE);

            long hours = Duration.between(
                    request.getStartTime(), request.getEndTime()).toHours();

            booking.setPrice(
                    "переговорная".equals(request.getPlaceType())
                            ? BigDecimal.valueOf(1200 * hours)
                            : BigDecimal.valueOf(200 * hours));

            bookingRepository.save(booking);
            return ResponseEntity.ok("Бронь успешно создана");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }
//
//    @PostMapping("/{id}/cancel")
//    @Transactional
//    public String cancelBooking(@PathVariable Long id) {
//        bookingRepository.cancelBooking(143);
//        return "redirect:/user";
//    }
}

// DTO классы
class BookingCheckRequest {
    private String date;
    private String startTime;
    private String endTime;
    private String placeType;
    // геттеры и сеттеры

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}

class BookingRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String placeType;
    // геттеры и сеттеры


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }
}


