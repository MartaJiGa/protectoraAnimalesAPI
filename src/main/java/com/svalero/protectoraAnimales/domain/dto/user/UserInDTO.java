package com.svalero.protectoraAnimales.domain.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserInDTO {
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El nombre de usuario solo puede estar compuesto por letras, números y guiones bajos")
    private String username;

    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede estar compuesto por letras y espacios")
    private String name;

    @Size(min = 3, max = 50, message = "El apellido debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede estar compuesto por letras y espacios")
    private String surname;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    private LocalDate dateOfBirth;

    @Email(message = "Debe proporcionar una dirección de correo válida")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;
}
