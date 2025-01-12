package com.svalero.protectoraAnimales.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @Column(name = "take_accessories")
    private boolean takeAccessories;

    @Column(name = "pick_up_date")
    @FutureOrPresent(message = "La fecha de recogida debe ser en el futuro o la de hoy")
    @NotNull(message = "La fecha de recogida no puede estar vacía")
    private LocalDate pickUpDate;

    @Column(name = "pick_up_time")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9])$", message = "La hora de recogida debe tener el formato HH:mm (24 horas)")
    @NotBlank(message = "La hora de recogida no puede estar vacía")
    private String pickUpTime;

    @ManyToOne
    @JoinColumn(name = "animal_id", referencedColumnName = "id", nullable = false)
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}
