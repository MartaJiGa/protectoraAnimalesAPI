package com.svalero.protectoraAnimales.domain.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserChangeEmailInDTO {
    @Email(message = "Debe proporcionar una dirección de correo válida")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;
}
