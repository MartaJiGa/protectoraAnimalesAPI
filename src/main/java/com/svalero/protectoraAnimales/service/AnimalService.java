package com.svalero.protectoraAnimales.service;

import java.util.List;
import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    public List<Animal> getAnimals(){
        return animalRepository.findAll();
    }

    public void saveAnimal(Animal animal){
        animalRepository.save(animal);
    }
}
