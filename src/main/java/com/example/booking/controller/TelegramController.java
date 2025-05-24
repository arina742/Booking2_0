package com.example.booking.controller;

import com.example.booking.model.Booking;
import com.example.booking.repositories.BookingRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;


@RestController
@RequestMapping("/api/telegram")
public class TelegramController {

    private static final Logger log = LoggerFactory.getLogger(TelegramController.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private BookingRepository bookingRepository;

    @PostMapping("/send-notification")
    public ResponseEntity<?> sendNotification(
            @RequestBody TelegramNotificationRequest request,
            HttpSession session) {

        try {
            // 1. Проверяем авторизацию
            String phoneNumber = (String) session.getAttribute("login");
            if (phoneNumber == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 2. Проверяем chatId
            String chatId = request.getChatId();
            if (chatId == null || chatId.isEmpty()) {
                chatId = (String) session.getAttribute("telegramChatId");
                if (chatId == null) {
                    return ResponseEntity.badRequest().body("Chat ID не указан");
                }
            }

            // 3. Отправляем сообщение
            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                    botToken,
                    chatId,
                    URLEncoder.encode(request.getMessage(), StandardCharsets.UTF_8)
            );

            log.info("Отправка сообщения в Telegram для chatId: {}", chatId);

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(
                            HttpRequest.newBuilder()
                                    .uri(URI.create(url))
                                    .timeout(Duration.ofSeconds(5))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );

            log.info("Ответ Telegram: {}", response.body());

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Ошибка отправки уведомления", e);
            return ResponseEntity.internalServerError().body("Ошибка сервера");
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TelegramNotificationRequest {
        private String chatId;
        private String message;
    }
}