package com.example.booking.services;

import com.example.booking.model.UserCredentials;
import com.example.booking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public boolean authenticate(String login, String password) {
        UserCredentials userCredentials = userRepository.findByLogin(login);
        if (userCredentials == null) return false;
        return userCredentials.getPassword().equals(password);
    }
}
