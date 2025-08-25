package com.svalero.protectoraAnimales.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "main_site")
    private boolean mainSite;

    @Column
    @NotBlank(message = "La dirección no puede estar vacía")
    private String address;

    @Column(name = "zip_code")
    @Pattern(regexp = "^(0[1-9]|[1-4][0-9]|5[0-2])[0-9]{3}$", message = "El código postal debe ser válido en España (01000 a 52999)")
    @NotBlank(message = "El código postal no puede estar vacío")
    private String zipCode;

    @Column
    @Pattern(regexp = "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+", message = "La ciudad solo puede estar compuesta por letras y espacios")
    @NotBlank(message = "La ciudad no puede estar vacía")
    private String city;

    @Column
    private String description;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @OneToMany(mappedBy = "location")
    @JsonBackReference(value = "locations_animals")
    private List<Animal> animals;
}
