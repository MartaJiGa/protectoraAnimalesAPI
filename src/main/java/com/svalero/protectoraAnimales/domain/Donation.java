package com.svalero.protectoraAnimales.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "donation_date")
    private LocalDate donationDate;

    @Column
    private float quantity;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "split_payment")
    private boolean splitPayment;

    @Column(name = "split_payment_quantity")
    private int splitPaymentQuantity;
}
