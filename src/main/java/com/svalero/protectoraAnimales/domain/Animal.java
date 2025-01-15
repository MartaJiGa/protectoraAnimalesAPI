package com.svalero.protectoraAnimales.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 2 y 20 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede estar compuesto por letras y espacios")
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @Column
    @Pattern(regexp = "Perro|Gato|Hurón|Conejo", message = "La especie debe ser: Perro, Gato, Hurón o Conejo")
    @NotBlank(message = "La especie no puede estar vacía")
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
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @OneToMany(mappedBy = "animal")
    @JsonBackReference(value = "animal_adoptions")
    private List<Adoption> adoptions;
}
