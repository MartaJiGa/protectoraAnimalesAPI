package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Donation;
import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.domain.dto.donation.DonationInDTO;
import com.svalero.protectoraAnimales.domain.dto.donation.DonationOutDTO;
import com.svalero.protectoraAnimales.domain.dto.donation.DonationSplitPaymentInDTO;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.DonationRepository;
import com.svalero.protectoraAnimales.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DonationService {
    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    // region GET requests
    public Donation findById(long donationId){
        return donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donación con id " + donationId + " no encontrada."));
    }
    public List<DonationOutDTO> getDonations(){
        List<Donation> donations = donationRepository.findAll();
        if (donations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron donaciones.");
        }

        List<DonationOutDTO> donationOutDTOS = modelMapper.map(donations, new TypeToken<List<DonationOutDTO>>(){}.getType());
        return donationOutDTOS;
    }
    public List<DonationOutDTO> findByDonationDate(LocalDate donationDate){
        List<Donation> donations = donationRepository.findByDonationDate(donationDate);
        if (donations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron donaciones en la fecha " + donationDate);
        }

        List<DonationOutDTO> donationOutDTOS = modelMapper.map(donations, new TypeToken<List<DonationOutDTO>>(){}.getType());
        return donationOutDTOS;
    }
    public List<DonationOutDTO> findByUserId(long userId){
        List<Donation> donations = donationRepository.findByUserId(userId);
        if (donations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron donaciones del usuario " + userId);
        }

        List<DonationOutDTO> donationOutDTOS = modelMapper.map(donations, new TypeToken<List<DonationOutDTO>>(){}.getType());
        return donationOutDTOS;
    }
    public List<DonationOutDTO> findByDonationDateAndUserId(LocalDate donationDate, long userId) {
        List<Donation> donations = donationRepository.findByDonationDateAndUserId(donationDate, userId);
        if (donations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron donaciones en la fecha " + donationDate + " del usuario " + userId);
        }

        List<DonationOutDTO> donationOutDTOS = modelMapper.map(donations, new TypeToken<List<DonationOutDTO>>(){}.getType());
        return donationOutDTOS;
    }
    // endregion

    // region POST request
    public DonationOutDTO saveDonation(long userId, DonationInDTO donationInDTO){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        Donation donation = modelMapper.map(donationInDTO, Donation.class);

        donation.setUser(user);
        donation.setDonationDate(LocalDate.now());
        donationRepository.save(donation);

        DonationOutDTO donationOutDTO = modelMapper.map(donation, DonationOutDTO.class);
        return donationOutDTO;
    }
    // endregion

    // region DELETE request
    public void removeDonation(long donationId){
        if (!donationRepository.existsById(donationId)) {
            throw new ResourceNotFoundException("Donación con id " + donationId + " no encontrada.");
        }
        donationRepository.deleteById(donationId);
    }
    // endregion

    // region PUT request
    public DonationOutDTO modifyDonation(long donationId, long userId, DonationInDTO donationInDTO) {
        Donation existingDonation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donación con id " + donationId + " no encontrada."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        Donation donation = modelMapper.map(donationInDTO, Donation.class);

        existingDonation.setQuantity(donation.getQuantity());
        existingDonation.setPaymentType(donation.getPaymentType());
        existingDonation.setSplitPayment(donation.isSplitPayment());
        existingDonation.setUser(user);

        donationRepository.save(existingDonation);

        DonationOutDTO donationOutDTO = modelMapper.map(existingDonation, DonationOutDTO.class);
        return donationOutDTO;
    }
    // endregion

    // region PATCH request
    public DonationOutDTO splitPayment(long donationId, DonationSplitPaymentInDTO donationSplitPayment) {
        Donation existingDonation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donación con id " + donationId + " no encontrada."));

        existingDonation.setSplitPayment(donationSplitPayment.isSplitPayment());
        existingDonation.setSplitPaymentQuantity(donationSplitPayment.getSplitPaymentQuantity());

        donationRepository.save(existingDonation);

        DonationOutDTO donationOutDTO = modelMapper.map(existingDonation, DonationOutDTO.class);
        return donationOutDTO;
    }
}
