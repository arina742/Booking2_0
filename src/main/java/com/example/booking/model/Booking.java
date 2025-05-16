package com.example.booking.model;

import jakarta.persistence.*;

import java.util.Date;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate; // или LocalDateTime, в зависимости от выбранного варианта

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "place_type", nullable = false, length = 50)
    private String placeType;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    // Конструкторы
    public Booking() {
    }

    public Booking(LocalDate date, LocalTime startTime, LocalTime endTime,
                   String placeType, String phoneNumber, BigDecimal price, String status) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.placeType = placeType;
        this.phoneNumber = phoneNumber;
        this.price = price;
        this.status = status;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", placeType='" + placeType + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}