package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    // region GET requests
    @GetMapping("/animals")
    public List<Animal> findAll(@RequestParam(defaultValue = "") String species, @RequestParam(defaultValue = "0") int age, @RequestParam(defaultValue = "") String size){
        if(!species.isEmpty() && age == 0 && size.isEmpty()){
            animalService.getAnimalBySpecies(species);
        }
        else if(species.isEmpty() && age != 0 && size.isEmpty()){
            animalService.getAnimalByAge(age);
        }
        else if(species.isEmpty() && age == 0 && !size.isEmpty()){
            animalService.getAnimalBySize(size);
        }
        else if(!species.isEmpty() && age != 0 && size.isEmpty()){
            animalService.getAnimalBySpeciesAndAge(species, age);
        }
        else if(!species.isEmpty() && age == 0 && !size.isEmpty()){
            animalService.getAnimalBySpeciesAndSize(species, size);
        }
        else if(species.isEmpty() && age != 0 && !size.isEmpty()){
            animalService.getAnimalByAgeAndSize(age, size);
        }
        else if(!species.isEmpty() && age != 0 && !size.isEmpty()){
            animalService.getAnimalBySpeciesAndAgeAndSize(species, age, size);
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
}
