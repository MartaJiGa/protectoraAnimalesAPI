package com.svalero.protectoraAnimales.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity(name = "animals")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "incorporation_Date")
    private LocalDate incorporationDate;

    @Column
    private String name;

    @Column
    private String species;

    @Column
    private int age;

    @Column
    private String breed;

    @Column
    private String size;

    @Column
    private boolean neutered;

    @Column
    private float price;

    @Column
    private String description;
}
