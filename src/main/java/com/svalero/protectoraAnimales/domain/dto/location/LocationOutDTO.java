package com.svalero.protectoraAnimales.domain.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LocationOutDTO {
    private long id;
    private String address;
    private String city;
}
