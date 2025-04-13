package com.example.booking.controller;

import com.example.booking.model.UserCredentials;
import com.example.booking.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")  // Базовый путь для всех методов
public class UserController {

    private final UserService userService;

    // Внедрение сервиса через конструктор
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Регистрация пользователя (POST /api/users/register)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserCredentials request) {
        userService.registerUser(request.getLogin(), request.getPassword());
        return ResponseEntity.ok("Пользователь зарегистрирован!");
    }

    // Получение данных пользователя (GET /api/users/{login})
    @GetMapping("/{login}")
    public ResponseEntity<UserCredentials> getUser(@PathVariable String login) {
        UserCredentials user = userService.findByLogin(login);
        return ResponseEntity.ok(user);
    }

}
