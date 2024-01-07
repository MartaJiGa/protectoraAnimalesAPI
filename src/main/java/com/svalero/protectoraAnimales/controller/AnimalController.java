package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.ErrorResponse;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    // region GET requests
    @GetMapping("/animal/{animalId}")
    public Animal getAnimal(@PathVariable long animalId) throws ResourceNotFoundException {
        Optional<Animal> optionalAnimal = animalService.findById(animalId);
        return optionalAnimal.orElseThrow(()->new ResourceNotFoundException(animalId));
    }
    @GetMapping("/animals")
    public List<Animal> findAll(@RequestParam(defaultValue = "") String species, @RequestParam(defaultValue = "0") int age, @RequestParam(defaultValue = "") String size){
        if(!species.isEmpty() && age == 0 && size.isEmpty()){
            return animalService.findBySpecies(species);
        }
        else if(species.isEmpty() && age != 0 && size.isEmpty()){
            return animalService.findByAge(age);
        }
        else if(species.isEmpty() && age == 0 && !size.isEmpty()){
            return animalService.findBySize(size);
        }
        else if(!species.isEmpty() && age != 0 && size.isEmpty()){
            return animalService.findBySpeciesAndAge(species, age);
        }
        else if(!species.isEmpty() && age == 0 && !size.isEmpty()){
            return animalService.findBySpeciesAndSize(species, size);
        }
        else if(species.isEmpty() && age != 0 && !size.isEmpty()){
            return animalService.findByAgeAndSize(age, size);
        }
        else if(!species.isEmpty() && age != 0 && !size.isEmpty()){
            return animalService.findBySpeciesAndAgeAndSize(species, age, size);
        }
        return animalService.getAnimals();
    }
    // endregion

    //region POST request
    @PostMapping("/animals")
    public void saveAnimal(@RequestBody Animal animal){
        animalService.saveAnimal(animal);
    }
    //endregion

    //region DELETE request
    @DeleteMapping("/animal/{animalId}")
    public void removeProduct(@PathVariable long animalId){
        animalService.removeAnimal(animalId);
    }
    //endregion

    //region PUT request
    @PutMapping("/animal/{animalId}")
    public void modifyAnimal(@RequestBody Animal animal, @PathVariable long animalId){
        animalService.modifyAnimal(animal, animalId);
    }
    //endregion

    //region EXCEPTION HANDLER
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException resNotFoundEx){
        ErrorResponse errorResponse = new ErrorResponse(404, resNotFoundEx.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    // endregion
}
