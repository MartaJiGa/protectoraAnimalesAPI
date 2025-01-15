package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Adoption;
import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.dto.AnimalInDTO;
import com.svalero.protectoraAnimales.domain.dto.AnimalOutDTO;
import com.svalero.protectoraAnimales.service.AdoptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    // region GET requests
    @GetMapping("/adoption/{adoptionId}")
    public Adoption getAdoption(@PathVariable long adoptionId) {
        return adoptionService.findById(adoptionId);
    }
    @GetMapping("/adoptions")
    public ResponseEntity<List<Adoption>> findAll(@RequestParam(required = false) LocalDate adoptionDate, @RequestParam(defaultValue = "0") long userId, @RequestParam(defaultValue = "0") long animalId){
        List<Adoption> adoptions;

        if (adoptionDate != null && userId == 0 && animalId == 0) {
            adoptions = adoptionService.findByAdoptionDate(adoptionDate);
        }
        else if (adoptionDate == null && userId != 0 && animalId == 0) {
            adoptions = adoptionService.findByUserId(userId);
        }
        else if (adoptionDate == null && userId == 0 && animalId != 0) {
            adoptions = adoptionService.findByAnimalId(animalId);
        }
        else if (adoptionDate != null && userId != 0 && animalId == 0) {
            adoptions = adoptionService.findByAdoptionDateAndUserId(adoptionDate, userId);
        }
        else if (adoptionDate != null && userId == 0 && animalId != 0) {
            adoptions = adoptionService.findByAdoptionDateAndAnimalId(adoptionDate, animalId);
        }
        else if (adoptionDate == null && userId != 0 && animalId != 0) {
            adoptions = adoptionService.findByAnimalIdAndUserId(animalId, userId);
        }
        else if (adoptionDate != null && userId != 0 && animalId != 0) {
            adoptions = adoptionService.findByAdoptionDateAndAnimalIdAndUserId(adoptionDate, animalId, userId);
        }
        else {
            adoptions = adoptionService.getAdoptions();
        }

        return ResponseEntity.ok(adoptions);
    }
    // endregion

    //region POST request
    @PostMapping("/adoptions")
    public ResponseEntity<Adoption> saveAdoption(@PathVariable long userId, @PathVariable long animalId, @Valid @RequestBody Adoption adoption) {
        Adoption savedAdoption = adoptionService.saveAdoption(userId, animalId, adoption);
        return new ResponseEntity<>(savedAdoption, HttpStatus.CREATED);
    }
    // endregion

    //region DELETE request
    @DeleteMapping("/adoption/{adoptionId}")
    public ResponseEntity<Void> removeAdoption(@PathVariable long adoptionId){
        adoptionService.removeAdoption(adoptionId);
        return ResponseEntity.noContent().build();
    }
    // endregion

    //region PUT request
    @PutMapping("/adoption/{adoptionId}")
    public ResponseEntity<Adoption> modifyAdoption(@PathVariable long adoptionId, @Valid @RequestBody Adoption adoption){
        adoptionService.modifyAdoption(adoptionId, adoption);
        return ResponseEntity.ok(adoption);
    }
    @PutMapping("/adoption/{adoptionId}/animal/{animalId}")
    public ResponseEntity<Adoption> modifyAdoptionAnimal(@PathVariable long adoptionId, @PathVariable long animalId) {
        Adoption modifiedAdoptionAnimal = adoptionService.modifyAdoptionAnimal(adoptionId, animalId);
        return ResponseEntity.ok(modifiedAdoptionAnimal);
    }
    @PutMapping("/adoption/{adoptionId}/user/{userId}")
    public ResponseEntity<Adoption> modifyAdoptionUser(@PathVariable long adoptionId, @PathVariable long userId) {
        Adoption modifiedAdoptionUser = adoptionService.modifyAdoptionUser(adoptionId, userId);
        return ResponseEntity.ok(modifiedAdoptionUser);
    }
    // endregion
}
