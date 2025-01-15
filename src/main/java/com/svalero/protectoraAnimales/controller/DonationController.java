package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Donation;
import com.svalero.protectoraAnimales.domain.dto.DonationOutDTO;
import com.svalero.protectoraAnimales.service.DonationService;
import jakarta.validation.Valid;
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

    // region GET requests
    @GetMapping("/donation/{donationId}")
    public Donation getDonation(@PathVariable long donationId) {
        return donationService.findById(donationId);
    }
    @GetMapping("/donations")
    public ResponseEntity<List<DonationOutDTO>> findAll(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate donationDate, @RequestParam(defaultValue = "0") long userId){
        List<DonationOutDTO> donations;

        if (donationDate != null && userId == 0) {
            donations = donationService.findByDonationDate(donationDate);
        }
        else if (donationDate == null && userId != 0) {
            donations = donationService.findByUserId(userId);
        }
        else if (donationDate != null && userId != 0) {
            donations = donationService.findByDonationDateAndUserId(donationDate, userId);
        }
        else {
            donations = donationService.getDonations();
        }

        return ResponseEntity.ok(donations);
    }
    // endregion

    //region POST request
    @PostMapping("/donations/user/{userId}")
    public ResponseEntity<DonationOutDTO> saveDonation(@PathVariable long userId, @Valid @RequestBody Donation donation) {
        DonationOutDTO savedDonation = donationService.saveDonation(userId, donation);
        return new ResponseEntity<>(savedDonation, HttpStatus.CREATED);
    }
    // endregion

    //region DELETE request
    @DeleteMapping("/donation/{donationId}")
    public ResponseEntity<Void> removeDonation(@PathVariable long donationId){
        donationService.removeDonation(donationId);
        return ResponseEntity.noContent().build();
    }
    // endregion

    //region PUT request
    @PutMapping("/donation/{donationId}/user/{userId}")
    public ResponseEntity<DonationOutDTO> modifyAdoption(@PathVariable long donationId, @PathVariable long userId, @Valid @RequestBody Donation donation){
        DonationOutDTO updatedDonation = donationService.modifyDonation(donationId, userId, donation);
        return ResponseEntity.ok(updatedDonation);
    }
    // endregion
}
