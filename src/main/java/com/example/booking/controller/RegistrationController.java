package com.example.booking.controller;

import com.example.booking.repositories.UserRepository;
import com.example.booking.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registration")
    public String registration(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String password2,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Проверка совпадения паролей
        if (!password.equals(password2)) {
            model.addAttribute("error", "Пароли не совпадают");
            model.addAttribute("username", username); // Сохраняем введенный номер
            return "registration";
        }

        // Проверка формата номера телефона
        String formattedPhone = authService.getFormatNumber(username);
        if (formattedPhone.length() != 12) { // +7 и 10 цифр
            model.addAttribute("error", "Введите корректный номер телефона");
            model.addAttribute("username", username);
            return "registration";
        }

        // Попытка регистрации
        boolean isRegistered = authService.register(formattedPhone, password);

        if (!isRegistered) {
            model.addAttribute("error", "Пользователь с таким номером уже существует");
            model.addAttribute("username", username);
            return "registration";
        }

        // При успешной регистрации
        redirectAttributes.addFlashAttribute("registrationSuccess", "Регистрация прошла успешно! Теперь вы можете войти.");
        return "redirect:/login";
    }
}
