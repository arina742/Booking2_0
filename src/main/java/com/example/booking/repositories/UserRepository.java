package com.example.booking.repositories;

import com.example.booking.model.UserCredentials;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface UserRepository extends JpaRepository<UserCredentials, Long> {
    public UserCredentials findByLogin(String login);
    boolean existsByLogin(String login);
}
