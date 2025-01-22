package com.svalero.protectoraAnimales.domain.dto.adoption;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AdoptionChangePickUpInDTO {

    @FutureOrPresent(message = "La fecha de recogida debe ser en el futuro o la de hoy")
    @NotNull(message = "La fecha de recogida no puede estar vacía")
    private LocalDate pickUpDate;

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9])$", message = "La hora de recogida debe tener el formato HH:mm (24 horas)")
    @NotBlank(message = "La hora de recogida no puede estar vacía")
    private String pickUpTime;
}
