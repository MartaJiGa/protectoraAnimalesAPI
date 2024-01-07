package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.repository.AdoptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdoptionService {
    @Autowired
    private AdoptionRepository adoptionRepository;
}
