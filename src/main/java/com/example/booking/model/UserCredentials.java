package com.example.booking.model;


import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true, nullable = false, length = 50) // Логин уникален и обязателен
    private String login;

    @Column(nullable = false) // Пароль обязателен
    private String password;
    public UserCredentials(){
        
    }

    public UserCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
