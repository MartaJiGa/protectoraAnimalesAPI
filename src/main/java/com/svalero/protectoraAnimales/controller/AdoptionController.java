package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Adoption;
import com.svalero.protectoraAnimales.domain.dto.AdoptionOutDTO;
import com.svalero.protectoraAnimales.service.AdoptionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(AdoptionController.class);

    // region GET requests
    @GetMapping("/adoption/{adoptionId}")
    public Adoption getAdoption(@PathVariable long adoptionId) {
        logger.info("BEGIN getAdoption()");
        Adoption adoption = adoptionService.findById(adoptionId);
        logger.info("END getAdoption()");
        return adoption;
    }
    @GetMapping("/adoptions")
    public ResponseEntity<List<AdoptionOutDTO>> findAllAdoptions(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate adoptionDate, @RequestParam(defaultValue = "0") long userId, @RequestParam(defaultValue = "0") long animalId){
        List<AdoptionOutDTO> adoptions;

        if (adoptionDate != null && userId == 0 && animalId == 0) {
            logger.info("BEGIN findAllAdoptions() -> ByAdoptionDate");
            adoptions = adoptionService.findByAdoptionDate(adoptionDate);
            logger.info("END findAllAdoptions() -> ByAdoptionDate");
        }
        else if (adoptionDate == null && userId != 0 && animalId == 0) {
            logger.info("BEGIN findAllAdoptions() -> ByUserId");
            adoptions = adoptionService.findByUserId(userId);
            logger.info("END findAllAdoptions() -> ByUserId");
        }
        else if (adoptionDate == null && userId == 0 && animalId != 0) {
            logger.info("BEGIN findAllAdoptions() -> ByAnimalId");
            adoptions = adoptionService.findByAnimalId(animalId);
            logger.info("END findAllAdoptions() -> ByAnimalId");
        }
        else if (adoptionDate != null && userId != 0 && animalId == 0) {
            logger.info("BEGIN findAllAdoptions() -> ByAdoptionDateAndUserId");
            adoptions = adoptionService.findByAdoptionDateAndUserId(adoptionDate, userId);
            logger.info("END findAllAdoptions() -> ByAdoptionDateAndUserId");
        }
        else if (adoptionDate != null && userId == 0 && animalId != 0) {
            logger.info("BEGIN findAllAdoptions() -> ByAdoptionDateAndAnimalId");
            adoptions = adoptionService.findByAdoptionDateAndAnimalId(adoptionDate, animalId);
            logger.info("END findAllAdoptions() -> ByAdoptionDateAndAnimalId");
        }
        else if (adoptionDate == null && userId != 0 && animalId != 0) {
            logger.info("BEGIN findAllAdoptions() -> ByAnimalIdAndUserId");
            adoptions = adoptionService.findByAnimalIdAndUserId(animalId, userId);
            logger.info("END findAllAdoptions() -> ByAnimalIdAndUserId");
        }
        else if (adoptionDate != null && userId != 0 && animalId != 0) {
            logger.info("BEGIN findAllAdoptions() -> ByAdoptionDateAndAnimalIdAndUserId");
            adoptions = adoptionService.findByAdoptionDateAndAnimalIdAndUserId(adoptionDate, animalId, userId);
            logger.info("END findAllAdoptions() -> ByAdoptionDateAndAnimalIdAndUserId");
        }
        else {
            logger.info("BEGIN findAllAdoptions()");
            adoptions = adoptionService.getAdoptions();
            logger.info("END findAllAdoptions()");
        }

        return ResponseEntity.ok(adoptions);
    }
    // endregion

    //region POST request
    @PostMapping("/adoptions/user/{userId}/animal/{animalId}")
    public ResponseEntity<AdoptionOutDTO> saveAdoption(@PathVariable long userId, @PathVariable long animalId, @Valid @RequestBody Adoption adoption) {
        logger.info("BEGIN saveAdoption()");
        AdoptionOutDTO savedAdoption = adoptionService.saveAdoption(userId, animalId, adoption);
        logger.info("END saveAdoption()");
        return new ResponseEntity<>(savedAdoption, HttpStatus.CREATED);
    }
    // endregion

    //region DELETE request
    @DeleteMapping("/adoption/{adoptionId}")
    public ResponseEntity<Void> removeAdoption(@PathVariable long adoptionId){
        logger.info("BEGIN removeAdoption()");
        adoptionService.removeAdoption(adoptionId);
        logger.info("END removeAdoption()");
        return ResponseEntity.noContent().build();
    }
    // endregion

    //region PUT request
    @PutMapping("/adoption/{adoptionId}/user/{userId}/animal/{animalId}")
    public ResponseEntity<AdoptionOutDTO> modifyAdoption(@PathVariable long adoptionId, @PathVariable long animalId, @PathVariable long userId, @Valid @RequestBody Adoption adoption){
        logger.info("BEGIN modifyAdoption()");
        AdoptionOutDTO updatedAdoption = adoptionService.modifyAdoption(adoptionId, animalId, userId, adoption);
        logger.info("END modifyAdoption()");
        return ResponseEntity.ok(updatedAdoption);
    }
    // endregion
}
