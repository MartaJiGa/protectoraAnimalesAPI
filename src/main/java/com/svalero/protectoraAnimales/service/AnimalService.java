package com.svalero.protectoraAnimales.service;

import java.util.List;
import java.util.Optional;

import com.svalero.protectoraAnimales.domain.Animal;
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

    public List<Animal> getAnimalBySpecies(String species){
        return animalRepository.findBySpecies(species);
    }

    public List<Animal> findByAge(int age){
        return animalRepository.findByAge(age);
    }

    public List<Animal> getAnimalBySize(String size){
        return animalRepository.findBySize(size);
    }

    public List<Animal> getAnimalBySpeciesAndAge(String species, int age){
        return animalRepository.findBySpeciesAndAge(species, age);
    }

    public List<Animal> getAnimalBySpeciesAndSize(String species, String size){
        return animalRepository.findBySpeciesAndSize(species, size);
    }

    public List<Animal> getAnimalByAgeAndSize(int age, String species){
        return animalRepository.findByAgeAndSize(age, species);
    }

    public List<Animal> getAnimalBySpeciesAndAgeAndSize(String species, int age, String size){
        return animalRepository.findBySpeciesAndAgeAndSize(species, age, size);
    }

    public Optional<Animal> getAnimalById(long animalId){
        return animalRepository.findById(animalId);
    }
    // endregion

    // region POST request
    public void saveAnimal(Animal animal){
        animalRepository.save(animal);
    }
    // endregion

    // region DELETE request
    public void removeAnimal(long animalId){
        animalRepository.deleteById(animalId);
    }
    // endregion

    // region PUT request
    public void modifyAnimal(Animal newAnimal, long animalId){
        Optional<Animal> animal = animalRepository.findById(animalId);

        if(animal.isPresent()){
            Animal existingAnimal = animal.get();
            existingAnimal.setName(newAnimal.getName());
            existingAnimal.setSpecies(newAnimal.getSpecies());
            existingAnimal.setAge(newAnimal.getAge());
            existingAnimal.setBreed(newAnimal.getBreed());
            existingAnimal.setSize(newAnimal.getSize());
            existingAnimal.setNeutered(newAnimal.isNeutered());
            existingAnimal.setPrice(newAnimal.getPrice());
            existingAnimal.setDescription(newAnimal.getDescription());

            animalRepository.save(existingAnimal);
        }
    }
    // endregion
}
