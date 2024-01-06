package com.svalero.protectoraAnimales.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    private String address;

    @Column(name = "zip_code")
    private int zipCode;

    @Column
    private String city;

    @Column
    private String description;
}
