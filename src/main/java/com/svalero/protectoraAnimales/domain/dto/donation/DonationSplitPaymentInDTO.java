package com.svalero.protectoraAnimales.domain.dto.donation;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DonationSplitPaymentInDTO {

    private boolean splitPayment;

    @Max(value = 12, message = "La división de pagos debe ser como máximo de 12")
    private int splitPaymentQuantity;
}
