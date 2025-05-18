package com.example.booking.repositories;

import com.example.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;

@Repository

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByDate(LocalDate date);
    List<Booking> findByPhoneNumber(String phoneNumber);

    List<Booking> findByDateAndPlaceType(LocalDate date, String placeType);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.date = :date AND " +
            "b.placeType = :placeType AND " +
            "((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findOverlappingBookings(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("placeType") String placeType);
}