package com.svalero.protectoraAnimales.domain.dto.donation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DonationOutDTO {
    private long id;
    private LocalDate donationDate;
    private float quantity;
    private String paymentType;
    private boolean splitPayment;
    private int splitPaymentQuantity;

    // From user relationship:
    private long userId;
    private String userUsername;
    private String userEmail;
}
