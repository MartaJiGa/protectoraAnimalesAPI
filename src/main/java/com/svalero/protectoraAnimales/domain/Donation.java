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


@Entity(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "donation_date")
    private LocalDate donationDate;

    @Column
    @Min(value = 1, message = "La cantidad tiene que ser como mínimo de 1")
    @NotNull(message = "La cantidad no puede estar vacía")
    private float quantity;

    @Column(name = "payment_type")
    @Pattern(regexp = "Tarjeta|Efectivo|Transferencia", message = "El tipo de pago debe ser: Tarjeta, Efectivo o Transferencia")
    @NotBlank(message = "El tipo de pago no puede estar vacío")
    private String paymentType;

    @Column(name = "split_payment")
    private boolean splitPayment;

    @Column(name = "split_payment_quantity")
    @Max(value = 12, message = "La división de pagos debe ser como máximo de 12")
    private int splitPaymentQuantity;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}
