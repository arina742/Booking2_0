package com.example.booking;

import com.example.booking.model.BookingStatus;
import com.example.booking.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class BookingStatusUpdater {

    @Autowired
    private BookingRepository bookingRepository;

    // Запускается каждые 30 минут
    @Scheduled(fixedRate = 180)
    @Transactional
    public void updateExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();

        bookingRepository.findAllActive().forEach(booking -> {
            LocalDateTime bookingDateTime = LocalDateTime.of(booking.getDate(), booking.getEndTime());

            if (bookingDateTime.isBefore(now)) {
                booking.setStatus(BookingStatus.COMPLETED);
                bookingRepository.save(booking);
            }
        });
    }
}