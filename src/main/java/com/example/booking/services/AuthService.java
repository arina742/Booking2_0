package com.example.booking.services;

import com.example.booking.model.UserCredentials;
import com.example.booking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService {
        final String REG_EMAIL = "^[a-zA-Z0-9._+&*-]+@[a-zA-Z0-9.-]\\.[a-zA-Z]{2,}$";

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
        Pattern pattern = Pattern.compile(REG_EMAIL);
        Matcher matcher = pattern.matcher(login);
        if(!matcher.matches()) return false;
        String hashPassword = argon2.hash(2, 65536, 1, password.toCharArray());
        UserCredentials newUser = new UserCredentials(login, hashPassword);
        userRepository.save(newUser);
        return true;
    }
}
