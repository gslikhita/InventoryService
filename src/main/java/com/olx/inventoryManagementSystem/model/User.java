package com.olx.inventoryManagementSystem.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users") // TODO: Table name singular or plural?
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    int id;

    String email;

    String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
