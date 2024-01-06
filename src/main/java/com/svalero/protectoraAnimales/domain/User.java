package com.svalero.protectoraAnimales.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String username;

    @Column
    private String name;

    @Column
    private String surname;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column
    private String email;
}
