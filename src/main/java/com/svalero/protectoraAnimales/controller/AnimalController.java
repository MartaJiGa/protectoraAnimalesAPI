package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.dto.AnimalInDTO;
import com.svalero.protectoraAnimales.domain.dto.AnimalOutDTO;
import com.svalero.protectoraAnimales.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    // region GET requests
    @GetMapping("/animal/{animalId}")
    public Animal getAnimal(@PathVariable long animalId) {
        return animalService.findById(animalId);
    }
    @GetMapping("/animals")
    public ResponseEntity<List<AnimalOutDTO>> findAll(@RequestParam(defaultValue = "") String species, @RequestParam(defaultValue = "0") int age, @RequestParam(defaultValue = "") String size){
        List<AnimalOutDTO> animals;

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
    @PostMapping("/location/{locationId}/animals")
    public ResponseEntity<AnimalOutDTO> saveAnimal(@PathVariable long locationId, @Valid @RequestBody AnimalInDTO animal) {
        AnimalOutDTO savedAnimal = animalService.saveAnimal(locationId, animal);
        return new ResponseEntity<>(savedAnimal, HttpStatus.CREATED);
    }
    // endregion

    //region DELETE request
    @DeleteMapping("/animal/{animalId}")
    public ResponseEntity<Void> removeAnimal(@PathVariable long animalId){
        animalService.removeAnimal(animalId);
        return ResponseEntity.noContent().build();
    }
    // endregion

    //region PUT request
    @PutMapping("/animal/{animalId}")
    public ResponseEntity<Animal> modifyAnimal(@Valid @RequestBody Animal animal, @PathVariable long animalId){
        animalService.modifyAnimal(animal, animalId);
        return ResponseEntity.ok(animal);
    }
    @PutMapping("/location/{locationId}/animal/{animalId}")
    public ResponseEntity<Animal> modifyAnimalLocation(@PathVariable long locationId, @PathVariable long animalId) {
        Animal modifiedAnimal = animalService.modifyAnimalLocation(animalId, locationId);
        return ResponseEntity.ok(modifiedAnimal);
    }
    // endregion
}
