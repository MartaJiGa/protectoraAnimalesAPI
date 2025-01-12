package com.svalero.protectoraAnimales.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El nombre de usuario solo puede estar compuesto por letras, números y guiones bajos")
    private String username;

    @Column
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede estar compuesto por letras y espacios")
    private String name;

    @Column
    @Size(min = 3, max = 50, message = "El apellido debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede estar compuesto por letras y espacios")
    private String surname;

    @Column(name = "date_of_birth")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    private LocalDate dateOfBirth;

    @Column
    @Email(message = "Debe proporcionar una dirección de correo válida")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @OneToMany(mappedBy = "user")
    private List<Adoption> adoptions;

    @OneToMany(mappedBy = "user")
    private List<Donation> donations;
}
