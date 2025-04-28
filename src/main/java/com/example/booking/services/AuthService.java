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

    @Autowired
    private UserRepository userRepository;
    Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    public boolean authenticate(String login, String password) {
        login=getFormatNumber(login);
        UserCredentials userCredentials = userRepository.findByLogin(login);
        if (userCredentials == null) return false;
        return argon2.verify(userCredentials.getPassword(), password.toCharArray());
    }

    public boolean register(String login, String password) {
        login = getFormatNumber(login);

        if (userRepository.existsByLogin(login)) return false;

        String hashPassword = argon2.hash(2, 65536, 1, password.toCharArray());
        UserCredentials newUser = new UserCredentials(login, hashPassword);
        userRepository.save(newUser);
        return true;
    }

    public String getFormatNumber(String login) {
        login = login.replaceAll("[^0-9]", "");
        if(login.length() == 10) {
            login = "+7"+login;
        } else if(login.length() == 11) {
            login = "+7"+login.substring(1);
        }else if(login.length() == 12) {
            login = "+7"+login.substring(2);
        }
        return login;
    }
}
