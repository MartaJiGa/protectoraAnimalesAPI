package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Donation;
import com.svalero.protectoraAnimales.domain.dto.DonationOutDTO;
import com.svalero.protectoraAnimales.service.DonationService;
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
public class DonationController {
    @Autowired
    private DonationService donationService;
    private final Logger logger = LoggerFactory.getLogger(DonationController.class);

    // region GET requests
    @GetMapping("/donation/{donationId}")
    public Donation getDonation(@PathVariable long donationId) {
        logger.info("BEGIN getDonation()");
        Donation donation = donationService.findById(donationId);
        logger.info("BEGIN getDonation()");
        return donation;
    }
    @GetMapping("/donations")
    public ResponseEntity<List<DonationOutDTO>> findAllDonations(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate donationDate, @RequestParam(defaultValue = "0") long userId){
        List<DonationOutDTO> donations;

        if (donationDate != null && userId == 0) {
            logger.info("BEGIN findAllDonations() -> ByDonationDate");
            donations = donationService.findByDonationDate(donationDate);
            logger.info("END findAllDonations() -> ByDonationDate");
        }
        else if (donationDate == null && userId != 0) {
            logger.info("BEGIN findAllDonations() -> ByUserId");
            donations = donationService.findByUserId(userId);
            logger.info("END findAllDonations() -> ByUserId");
        }
        else if (donationDate != null && userId != 0) {
            logger.info("BEGIN findAllDonations() -> ByDonationDateAndUserId");
            donations = donationService.findByDonationDateAndUserId(donationDate, userId);
            logger.info("END findAllDonations() -> ByDonationDateAndUserId");
        }
        else {
            logger.info("BEGIN findAllDonations()");
            donations = donationService.getDonations();
            logger.info("END findAllDonations()");
        }

        return ResponseEntity.ok(donations);
    }
    // endregion

    //region POST request
    @PostMapping("/donations/user/{userId}")
    public ResponseEntity<DonationOutDTO> saveDonation(@PathVariable long userId, @Valid @RequestBody Donation donation) {
        logger.info("BEGIN saveDonation()");
        DonationOutDTO savedDonation = donationService.saveDonation(userId, donation);
        logger.info("END saveDonation()");
        return new ResponseEntity<>(savedDonation, HttpStatus.CREATED);
    }
    // endregion

    //region DELETE request
    @DeleteMapping("/donation/{donationId}")
    public ResponseEntity<Void> removeDonation(@PathVariable long donationId){
        logger.info("BEGIN removeDonation()");
        donationService.removeDonation(donationId);
        logger.info("END removeDonation()");
        return ResponseEntity.noContent().build();
    }
    // endregion

    //region PUT request
    @PutMapping("/donation/{donationId}/user/{userId}")
    public ResponseEntity<DonationOutDTO> modifyAdoption(@PathVariable long donationId, @PathVariable long userId, @Valid @RequestBody Donation donation){
        logger.info("BEGIN modifyAdoption()");
        DonationOutDTO updatedDonation = donationService.modifyDonation(donationId, userId, donation);
        logger.info("END modifyAdoption()");
        return ResponseEntity.ok(updatedDonation);
    }
    // endregion
}
