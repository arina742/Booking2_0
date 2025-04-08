package com.example.booking.services;

import com.example.booking.UserCredentialsRepository;
import com.example.booking.model.UserCredentials;

public class UserService {
    private final UserCredentialsRepository repository;

    public UserService(UserCredentialsRepository repository) {
        this.repository = repository;
    }

    public UserCredentials registerUser(String login, String password) {
        if (repository.existsByLogin(login)) {
            throw new RuntimeException("Login already exists!");
        }

        UserCredentials user = new UserCredentials(login, password);
        return repository.save(user);
    }

    public UserCredentials findByLogin(String login) {
        if(repository.existsByLogin(login)) {
            return repository.findByLogin(login);
        }else{
            return null;
        }
    }
}
