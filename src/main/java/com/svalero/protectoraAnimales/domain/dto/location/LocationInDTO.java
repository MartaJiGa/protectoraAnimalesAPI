package com.svalero.protectoraAnimales.domain.dto.location;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LocationInDTO {
    private boolean mainSite;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String address;

    @Pattern(regexp = "^(0[1-9]|[1-4][0-9]|5[0-2])[0-9]{3}$", message = "El código postal debe ser válido en España (01000 a 52999)")
    @NotBlank(message = "El código postal no puede estar vacío")
    private String zipCode;

    @Pattern(regexp = "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+", message = "La ciudad solo puede estar compuesta por letras y espacios")
    @NotBlank(message = "La ciudad no puede estar vacía")
    private String city;

    private String description;

    private Double latitude;

    private Double longitude;
}
