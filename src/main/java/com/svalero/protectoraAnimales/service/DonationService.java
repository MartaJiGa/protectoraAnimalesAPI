package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DonationService {
    @Autowired
    private DonationRepository donationRepository;
}
