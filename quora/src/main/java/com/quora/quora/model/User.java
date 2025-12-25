package com.quora.quora.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor

public class User {

    @Id
    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;        // <-- PK 

    @Column(nullable = false)
    private String password;        // encoded (BCrypt)

    @Column(nullable = false)
    private String email;

    // Optional convenience constructor
    public User(String username, String password, String role, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
