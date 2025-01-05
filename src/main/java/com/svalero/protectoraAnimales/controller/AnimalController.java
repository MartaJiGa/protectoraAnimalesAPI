package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.ErrorResponse;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    // region GET requests
    @GetMapping("/animal/{animalId}")
    public Animal getAnimal(@PathVariable long animalId) throws ResourceNotFoundException {
        Optional<Animal> optionalAnimal = Optional.ofNullable(animalService.findById(animalId));
        return optionalAnimal.orElseThrow(() -> new ResourceNotFoundException(animalId));
    }
    @GetMapping("/animals")
    public ResponseEntity<List<Animal>> findAll(@RequestParam(defaultValue = "") String species, @RequestParam(defaultValue = "0") int age, @RequestParam(defaultValue = "") String size){
        List<Animal> animals;

        if (!species.isEmpty() && age == 0 && size.isEmpty()) {
            animals = animalService.findBySpecies(species);
        }
        else if (species.isEmpty() && age != 0 && size.isEmpty()) {
            animals = animalService.findByAge(age);
        }
        else if (species.isEmpty() && age == 0 && !size.isEmpty()) {
            animals = animalService.findBySize(size);
        }
        else if (!species.isEmpty() && age != 0 && size.isEmpty()) {
            animals = animalService.findBySpeciesAndAge(species, age);
        }
        else if (!species.isEmpty() && age == 0 && !size.isEmpty()) {
            animals = animalService.findBySpeciesAndSize(species, size);
        }
        else if (species.isEmpty() && age != 0 && !size.isEmpty()) {
            animals = animalService.findByAgeAndSize(age, size);
        }
        else if (!species.isEmpty() && age != 0 && !size.isEmpty()) {
            animals = animalService.findBySpeciesAndAgeAndSize(species, age, size);
        } else {
            animals = animalService.getAnimals();
        }

        return ResponseEntity.ok(animals);
    }
    // endregion

    //region POST request
    @PostMapping("/animals")
    public ResponseEntity<Animal> saveAnimal(@Valid @RequestBody Animal animal){
        Animal savedAnimal = animalService.saveAnimal(animal);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAnimal);
    }
    //endregion

    //region DELETE request
    @DeleteMapping("/animal/{animalId}")
    public ResponseEntity<Void> removeProduct(@PathVariable long animalId){
        animalService.removeAnimal(animalId);
        return ResponseEntity.noContent().build();
    }
    //endregion

    //region PUT request
    @PutMapping("/animal/{animalId}")
    public ResponseEntity<Animal> modifyAnimal(@Valid @RequestBody Animal animal, @PathVariable long animalId){
        animalService.modifyAnimal(animal, animalId);
        return ResponseEntity.ok(animal);
    }
    //endregion

    //region EXCEPTION HANDLER

    // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorResponse errorResponse = new ErrorResponse(400, "Errores de validación", errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException resNotFoundEx){
        ErrorResponse errorResponse = new ErrorResponse(404, resNotFoundEx.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generalException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(500, "Error interno del servidor. Inténtalo más tarde.", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // endregion
}
