package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Adoption;
import com.svalero.protectoraAnimales.domain.dto.AdoptionOutDTO;
import com.svalero.protectoraAnimales.service.AdoptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ResponseEntity<List<AdoptionOutDTO>> findAll(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate adoptionDate, @RequestParam(defaultValue = "0") long userId, @RequestParam(defaultValue = "0") long animalId){
        List<AdoptionOutDTO> adoptions;

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
    @PostMapping("/adoptions/user/{userId}/animal/{animalId}")
    public ResponseEntity<AdoptionOutDTO> saveAdoption(@PathVariable long userId, @PathVariable long animalId, @Valid @RequestBody Adoption adoption) {
        AdoptionOutDTO savedAdoption = adoptionService.saveAdoption(userId, animalId, adoption);
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
    @PutMapping("/adoption/{adoptionId}/user/{userId}/animal/{animalId}")
    public ResponseEntity<AdoptionOutDTO> modifyAdoption(@PathVariable long adoptionId, @PathVariable long animalId, @PathVariable long userId, @Valid @RequestBody Adoption adoption){
        AdoptionOutDTO updatedAdoption = adoptionService.modifyAdoption(adoptionId, animalId, userId, adoption);
        return ResponseEntity.ok(updatedAdoption);
    }
    // endregion
}
