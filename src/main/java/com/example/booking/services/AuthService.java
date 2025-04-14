package com.example.booking.services;

import com.example.booking.model.UserCredentials;
import com.example.booking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public boolean authenticate(String login, String password) {
        UserCredentials userCredentials = userRepository.findByLogin(login);
        if (userCredentials == null) return false;
        return argon2.verify(userCredentials.getPassword(), password.toCharArray());
    }

    public boolean register(String login, String password) {
        UserCredentials userCredentials = userRepository.findByLogin(login);
        if (userRepository.existsByLogin(login)) return false;
        String hashPassword = argon2.hash(2, 65536, 1, password.toCharArray());
        UserCredentials newUser = new UserCredentials(login, hashPassword);
        userRepository.save(newUser);
        return false;
    }
}
