package com.example.booking.model;

public enum BookingStatus {
    ACTIVE,     // Активная бронь
    CANCELLED,  // Отменена пользователем
    COMPLETED ;  // Завершена (дата прошла)

    public String getName() {
        return name();
    }
}
