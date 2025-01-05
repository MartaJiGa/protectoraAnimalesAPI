package com.svalero.protectoraAnimales.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ErrorResponse {
    private int statusCode;
    private String message;
    private Map<String, String> validationErrors;
}
