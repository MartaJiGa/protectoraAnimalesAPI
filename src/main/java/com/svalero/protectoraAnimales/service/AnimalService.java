package com.svalero.protectoraAnimales.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    // region GET requests
    public List<Animal> getAnimals(){
        return animalRepository.findAll();
    }

    public List<Animal> findBySpecies(String species){
        return animalRepository.findBySpecies(species);
    }

    public List<Animal> findByAge(int age){
        return animalRepository.findByAge(age);
    }

    public List<Animal> findBySize(String size){
        return animalRepository.findBySize(size);
    }

    public List<Animal> findBySpeciesAndAge(String species, int age){
        return animalRepository.findBySpeciesAndAge(species, age);
    }

    public List<Animal> findBySpeciesAndSize(String species, String size){
        return animalRepository.findBySpeciesAndSize(species, size);
    }

    public List<Animal> findByAgeAndSize(int age, String species){
        return animalRepository.findByAgeAndSize(age, species);
    }

    public List<Animal> findBySpeciesAndAgeAndSize(String species, int age, String size){
        return animalRepository.findBySpeciesAndAgeAndSize(species, age, size);
    }

    public Animal findById(long animalId){
        return animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));
    }
    // endregion

    // region POST request
    public Animal saveAnimal(Animal animal){
        if (animal.getIncorporationDate() == null) {
            animal.setIncorporationDate(LocalDate.now());
        }
        return animalRepository.save(animal);
    }
    // endregion

    // region DELETE request
    public void removeAnimal(long animalId){
        animalRepository.deleteById(animalId);
    }
    // endregion

    // region PUT request
    public Animal modifyAnimal(Animal newAnimal, long animalId) {
        Animal existingAnimal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));

        existingAnimal.setName(newAnimal.getName());
        existingAnimal.setSpecies(newAnimal.getSpecies());
        existingAnimal.setAge(newAnimal.getAge());
        existingAnimal.setBreed(newAnimal.getBreed());
        existingAnimal.setSize(newAnimal.getSize());
        existingAnimal.setNeutered(newAnimal.isNeutered());
        existingAnimal.setPrice(newAnimal.getPrice());
        existingAnimal.setDescription(newAnimal.getDescription());

        return animalRepository.save(existingAnimal);
    }
    // endregion
}
