package com.svalero.protectoraAnimales.service;

import java.time.LocalDate;
import java.util.List;

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
    public Animal findById(long animalId){
        return animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));
    }
    public List<Animal> getAnimals(){
        List<Animal> animals = animalRepository.findAll();
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales.");
        }
        return animals;
    }
    public List<Animal> findBySpecies(String species){
        List<Animal> animals = animalRepository.findBySpecies(species);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de la especie " + species);
        }
        return animals;
    }
    public List<Animal> findByAge(int age){
        List<Animal> animals = animalRepository.findByAge(age);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de edad " + age);
        }
        return animals;
    }
    public List<Animal> findBySize(String size) {
        List<Animal> animals = animalRepository.findBySize(size);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de tamaño " + size);
        }
        return animals;
    }
    public List<Animal> findBySpeciesAndAge(String species, int age) {
        List<Animal> animals = animalRepository.findBySpeciesAndAge(species, age);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de la especie " + species + " de edad " + age);
        }
        return animals;
    }
    public List<Animal> findBySpeciesAndSize(String species, String size) {
        List<Animal> animals = animalRepository.findBySpeciesAndSize(species, size);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de la especie " + species + " y tamaño " + size);
        }
        return animals;
    }
    public List<Animal> findByAgeAndSize(int age, String size) {
        List<Animal> animals = animalRepository.findByAgeAndSize(age, size);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de edad " + age + " y tamaño " + size);
        }
        return animals;
    }
    public List<Animal> findBySpeciesAndAgeAndSize(String species, int age, String size) {
        List<Animal> animals = animalRepository.findBySpeciesAndAgeAndSize(species, age, size);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de la especie " + species + ", edad " + age + " y tamaño " + size);
        }
        return animals;
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
        if (!animalRepository.existsById(animalId)) {
            throw new ResourceNotFoundException("Animal con id " + animalId + " no encontrado.");
        }
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
