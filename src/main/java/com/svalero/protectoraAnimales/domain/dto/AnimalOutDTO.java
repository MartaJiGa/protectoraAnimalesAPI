package com.svalero.protectoraAnimales.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AnimalOutDTO {
    private long id;
    private LocalDate incorporationDate;
    private String name;
    private String species;
    private int age;
    private String breed;
    private String size;
    private boolean neutered;
    private float price;
    private String description;

    // From location relationship:
    private long locationId;
    private String locationCity;
}
