package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalInDTO;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalOutDTO;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalUpdateDTO;
import com.svalero.protectoraAnimales.service.AnimalService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class AnimalController {

    @Autowired
    private AnimalService animalService;
    private final Logger logger = LoggerFactory.getLogger(AnimalController.class);

    // region GET requests
    @GetMapping("/animal/{animalId}")
    public Animal getAnimal(@PathVariable long animalId) {
        logger.info("BEGIN getAnimal()");
        Animal animal = animalService.findById(animalId);
        logger.info("END getAnimal()");
        return animal;
    }
    @GetMapping("/animals")
    public ResponseEntity<List<AnimalOutDTO>> findAllAnimals(@RequestParam(defaultValue = "") String species,
                                                             @RequestParam(defaultValue = "0") int age,
                                                             @RequestParam(defaultValue = "") String size){
        List<AnimalOutDTO> animals;

        if (!species.isEmpty() && age == 0 && size.isEmpty()) {
            logger.info("BEGIN findAllAnimals() -> BySpecies");
            animals = animalService.findBySpecies(species);
            logger.info("END findAllAnimals() -> BySpecies");
        }
        else if (species.isEmpty() && age != 0 && size.isEmpty()) {
            logger.info("BEGIN findAllAnimals() -> ByAge");
            animals = animalService.findByAge(age);
            logger.info("END findAllAnimals() -> ByAge");
        }
        else if (species.isEmpty() && age == 0 && !size.isEmpty()) {
            logger.info("BEGIN findAllAnimals() -> BySize");
            animals = animalService.findBySize(size);
            logger.info("END findAllAnimals() -> BySize");
        }
        else if (!species.isEmpty() && age != 0 && size.isEmpty()) {
            logger.info("BEGIN findAllAnimals() -> BySpeciesAndAge");
            animals = animalService.findBySpeciesAndAge(species, age);
            logger.info("END findAllAnimals() -> BySpeciesAndAge");
        }
        else if (!species.isEmpty() && age == 0 && !size.isEmpty()) {
            logger.info("BEGIN findAllAnimals() -> BySpeciesAndSize");
            animals = animalService.findBySpeciesAndSize(species, size);
            logger.info("END findAllAnimals() -> BySpeciesAndSize");
        }
        else if (species.isEmpty() && age != 0 && !size.isEmpty()) {
            logger.info("BEGIN findAllAnimals() -> ByAgeAndSize");
            animals = animalService.findByAgeAndSize(age, size);
            logger.info("END findAllAnimals() -> ByAgeAndSize");
        }
        else if (!species.isEmpty() && age != 0 && !size.isEmpty()) {
            logger.info("BEGIN findAllAnimals() -> BySpeciesAndAgeAndSize");
            animals = animalService.findBySpeciesAndAgeAndSize(species, age, size);
            logger.info("END findAllAnimals() -> BySpeciesAndAgeAndSize");
        } else {
            logger.info("BEGIN findAllAnimals()");
            animals = animalService.getAnimals();
            logger.info("END findAllAnimals()");
        }

        return ResponseEntity.ok(animals);
    }
    @GetMapping("/animals/{locationId}")
    public ResponseEntity<List<AnimalOutDTO>> findUnadoptedAnimalsByLocation(@PathVariable long locationId){
        List<AnimalOutDTO> animals;

        logger.info("BEGIN findUnadoptedAnimalsByLocation()");
        animals = animalService.findUnadoptedAnimalsByLocation(locationId);
        logger.info("END findUnadoptedAnimalsByLocation()");

        return ResponseEntity.ok(animals);
    }
    // endregion

    // region POST request
    @PostMapping(value = "/location/{locationId}/animals", consumes = {"multipart/form-data"})
    public ResponseEntity<AnimalOutDTO> saveAnimal(@PathVariable long locationId,
                                                   @Valid @RequestPart("animal") AnimalInDTO animal,
                                                   @RequestPart(value = "image", required = false) MultipartFile imageFile) throws Exception {
        logger.info("BEGIN saveAnimal()");
        AnimalOutDTO savedAnimal = animalService.saveAnimal(locationId, animal, imageFile);
        logger.info("END saveAnimal()");
        return new ResponseEntity<>(savedAnimal, HttpStatus.CREATED);
    }
    // endregion

    // region DELETE request
    @DeleteMapping("/animal/{animalId}")
    public ResponseEntity<Void> removeAnimal(@PathVariable long animalId){
        logger.info("BEGIN removeAnimal()");
        animalService.removeAnimal(animalId);
        logger.info("END removeAnimal()");
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region PUT request
    @PutMapping(value = "/location/{locationId}/animal/{animalId}", consumes = {"multipart/form-data"})
    public ResponseEntity<AnimalOutDTO> modifyAnimalLocation(@Valid @RequestPart("animal") AnimalUpdateDTO animal,
                                                             @RequestPart(value = "image", required = false) MultipartFile imageFile,
                                                             @PathVariable long animalId,
                                                             @PathVariable long locationId) throws Exception {
        logger.info("BEGIN modifyAnimal()");
        AnimalOutDTO modifiedAnimal = animalService.modifyAnimal(animal, animalId, locationId, imageFile);
        logger.info("END modifyAnimal()");
        return ResponseEntity.ok(modifiedAnimal);
    }
    // endregion

    // region PATCH request
    @PatchMapping("/animal/{animalId}/return")
    public ResponseEntity<AnimalOutDTO> returnAnimal(@PathVariable long animalId){
        logger.info("BEGIN returnAnimal()");
        AnimalOutDTO returnedAnimal = animalService.returnAnimal(animalId);
        logger.info("END returnAnimal()");
        return ResponseEntity.ok(returnedAnimal);
    }
    // endregion
}
