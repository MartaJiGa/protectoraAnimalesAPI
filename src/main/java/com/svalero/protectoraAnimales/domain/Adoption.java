package com.svalero.protectoraAnimales.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity(name = "adoptions")
public class Adoption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "adoption_date")
    private LocalDate adoptionDate;

    @Column(name = "animal_id")
    private long animalId;

    @Column(name = "take_accessories")
    private boolean takeAccessories;

    @Column(name = "pick_up_date")
    private LocalDate pickUpDate;

    @Column(name = "pick_up_time")
    private String pickUpTime;
}
