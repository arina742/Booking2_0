package com.example.booking.repositories;

import com.example.booking.model.Booking;
import com.example.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = 'COMPLETED' " +
            "WHERE (b.status = 'ACTIVE') AND " +
            "(b.date < CURRENT_DATE OR " +
            "(b.date = CURRENT_DATE AND b.endTime < CURRENT_TIME))")
    void updateExpiredBookings();

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = 'CANCELLED' WHERE b.id = :id")
    void cancelBooking(@Param("id") long id);


}