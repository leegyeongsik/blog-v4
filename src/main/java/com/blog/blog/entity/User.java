package com.blog.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "User")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username" , nullable = false)
    private String username;

    @Column(name = "password" , nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;
    @OneToMany(mappedBy = "user" , fetch = FetchType.LAZY)
    private List<Comment> commentList = new ArrayList<>();

    public User(String username, String password , UserRoleEnum role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
