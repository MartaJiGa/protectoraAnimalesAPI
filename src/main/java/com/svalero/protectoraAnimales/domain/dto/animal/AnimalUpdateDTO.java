package com.svalero.protectoraAnimales.domain.dto.animal;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalUpdateDTO {
    private String name;
    private String species;
    private int age;
    private String breed;
    private String size;
    private boolean neutered;
    private boolean adopted;
    private float price;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate incorporationDate;
}
