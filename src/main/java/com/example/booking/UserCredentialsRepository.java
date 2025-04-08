package com.example.booking;

import com.example.booking.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {


    //нахождение пользователя по логину
    UserCredentials findByLogin(String login);

    //существует ли такой пользователь
    boolean existsByLogin(String login);
}
