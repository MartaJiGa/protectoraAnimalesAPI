package com.svalero.protectoraAnimales.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity(name = "animals")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "incorporation_date")
    private LocalDate incorporationDate;

    @Column
    @NotBlank(message = "El campo no puede estar vacío")
    private String name;

    @Column
    @NotBlank(message = "El campo no puede estar vacío")
    private String species;

    @Column
    @Min(value = 0)
    private int age;

    @Column
    private String breed;

    @Column
    @Pattern(regexp = "Pequeño|Mediano|Grande", message = "El tamaño debe ser: Pequeño, Mediano o Grande")
    private String size;

    @Column
    private boolean neutered;

    @Column
    @Min(value = 0, message = "El precio no puede ser negativo")
    private float price;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "animal")
    private List<Adoption> adoptions;
}
