package com.svalero.protectoraAnimales.domain.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AnimalInDTO {

    @Size(min = 3, max = 50, message = "El nombre debe tener entre 2 y 20 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede estar compuesto por letras y espacios")
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @Pattern(regexp = "Perro|Gato|Hurón|Conejo", message = "La especie debe ser: Perro, Gato, Hurón o Conejo")
    @NotBlank(message = "La especie no puede estar vacía")
    private String species;

    @Min(value = 0)
    private int age;

    private String breed;

    @Pattern(regexp = "Pequeño|Mediano|Grande", message = "El tamaño debe ser: Pequeño, Mediano o Grande")
    private String size;

    private boolean neutered;

    @Column
    private boolean adopted;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private float price;

    private String description;
}
