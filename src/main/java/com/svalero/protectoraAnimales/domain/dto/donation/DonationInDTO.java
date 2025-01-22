package com.svalero.protectoraAnimales.domain.dto.donation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DonationInDTO {

    @Min(value = 1, message = "La cantidad tiene que ser como mínimo de 1")
    @NotNull(message = "La cantidad no puede estar vacía")
    private float quantity;

    @Pattern(regexp = "Tarjeta|Efectivo|Transferencia", message = "El tipo de pago debe ser: Tarjeta, Efectivo o Transferencia")
    @NotBlank(message = "El tipo de pago no puede estar vacío")
    private String paymentType;

    private boolean splitPayment;

    @Max(value = 12, message = "La división de pagos debe ser como máximo de 12")
    private int splitPaymentQuantity;
}
