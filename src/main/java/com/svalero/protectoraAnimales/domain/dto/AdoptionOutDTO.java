package com.svalero.protectoraAnimales.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionOutDTO {

    private long id;
    private LocalDate adoptionDate;
    private boolean takeAccessories;
    private LocalDate pickUpDate;
    private String pickUpTime;

    // From animal relationship:
    private long animalId;
    private LocalDate animalIncorporationDate;
    private String animalName;
    private String animalSpecies;

    // From animal-location relationship:
    private long animalLocationId;
    private String animalLocationCity;

    // From user relationship:
    private long userId;
    private String userUsername;
    private String userEmail;
}
