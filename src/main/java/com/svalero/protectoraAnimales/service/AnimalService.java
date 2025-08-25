package com.svalero.protectoraAnimales.service;

import java.time.LocalDate;
import java.util.List;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalInDTO;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalOutDTO;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalUpdateDTO;
import com.svalero.protectoraAnimales.exception.runtime.NoChangeException;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.AnimalRepository;
import com.svalero.protectoraAnimales.repository.LocationRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ModelMapper modelMapper;

    // region GET requests
    public Animal findById(long animalId){
        return animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));
    }
    public List<AnimalOutDTO> getAnimals(){
        List<Animal> animals = animalRepository.findAll();
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales.");
        }

        List<AnimalOutDTO> animalOutDTOS = modelMapper.map(animals, new TypeToken<List<AnimalOutDTO>>(){}.getType());
        return animalOutDTOS;
    }
    public List<AnimalOutDTO> findBySpecies(String species){
        List<Animal> animals = animalRepository.findBySpecies(species);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de la especie " + species);
        }

        List<AnimalOutDTO> animalOutDTOS = modelMapper.map(animals, new TypeToken<List<AnimalOutDTO>>(){}.getType());
        return animalOutDTOS;
    }
    public List<AnimalOutDTO> findByAge(int age){
        List<Animal> animals = animalRepository.findByAge(age);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de edad " + age);
        }

        List<AnimalOutDTO> animalOutDTOS = modelMapper.map(animals, new TypeToken<List<AnimalOutDTO>>(){}.getType());
        return animalOutDTOS;
    }
    public List<AnimalOutDTO> findBySize(String size) {
        List<Animal> animals = animalRepository.findBySize(size);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de tamaño " + size);
        }

        List<AnimalOutDTO> animalOutDTOS = modelMapper.map(animals, new TypeToken<List<AnimalOutDTO>>(){}.getType());
        return animalOutDTOS;
    }
    public List<AnimalOutDTO> findBySpeciesAndAge(String species, int age) {
        List<Animal> animals = animalRepository.findBySpeciesAndAge(species, age);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de la especie " + species + " de edad " + age);
        }

        List<AnimalOutDTO> animalOutDTOS = modelMapper.map(animals, new TypeToken<List<AnimalOutDTO>>(){}.getType());
        return animalOutDTOS;
    }
    public List<AnimalOutDTO> findBySpeciesAndSize(String species, String size) {
        List<Animal> animals = animalRepository.findBySpeciesAndSize(species, size);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de la especie " + species + " y tamaño " + size);
        }

        List<AnimalOutDTO> animalOutDTOS = modelMapper.map(animals, new TypeToken<List<AnimalOutDTO>>(){}.getType());
        return animalOutDTOS;
    }
    public List<AnimalOutDTO> findByAgeAndSize(int age, String size) {
        List<Animal> animals = animalRepository.findByAgeAndSize(age, size);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de edad " + age + " y tamaño " + size);
        }

        List<AnimalOutDTO> animalOutDTOS = modelMapper.map(animals, new TypeToken<List<AnimalOutDTO>>(){}.getType());
        return animalOutDTOS;
    }
    public List<AnimalOutDTO> findBySpeciesAndAgeAndSize(String species, int age, String size) {
        List<Animal> animals = animalRepository.findBySpeciesAndAgeAndSize(species, age, size);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales de la especie " + species + ", edad " + age + " y tamaño " + size);
        }

        List<AnimalOutDTO> animalOutDTOS = modelMapper.map(animals, new TypeToken<List<AnimalOutDTO>>(){}.getType());
        return animalOutDTOS;
    }
    public List<AnimalOutDTO> findUnadoptedAnimalsByLocation(long locationId) {
        List<Animal> animals = animalRepository.findUnadoptedAnimalsByLocation(locationId);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales en adopción en la ubicación " + locationId);
        }

        List<AnimalOutDTO> animalOutDTOS = modelMapper.map(animals, new TypeToken<List<AnimalOutDTO>>(){}.getType());
        return animalOutDTOS;
    }
    // endregion

    // region POST request
    public AnimalOutDTO saveAnimal(long locationId, AnimalInDTO animalInDTO){

        try {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."));

            Animal animal = modelMapper.map(animalInDTO, Animal.class);
            animal.setLocation(location);
            animal.setIncorporationDate(LocalDate.now());
            animalRepository.save(animal);

            AnimalOutDTO animalOutDTO = modelMapper.map(animal, AnimalOutDTO.class);
            return animalOutDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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
    public AnimalOutDTO modifyAnimal(AnimalUpdateDTO animalInDTO, long animalId, long locationId) {
        Animal existingAnimal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."));

        Animal animal = modelMapper.map(animalInDTO, Animal.class);

        existingAnimal.setName(animal.getName());
        existingAnimal.setSpecies(animal.getSpecies());
        existingAnimal.setAge(animal.getAge());
        existingAnimal.setBreed(animal.getBreed());
        existingAnimal.setSize(animal.getSize());
        existingAnimal.setNeutered(animal.isNeutered());
        existingAnimal.setAdopted(animal.isAdopted());
        existingAnimal.setPrice(animal.getPrice());
        existingAnimal.setDescription(animal.getDescription());
        existingAnimal.setIncorporationDate(animal.getIncorporationDate());
        existingAnimal.setLocation(location);

        animalRepository.save(existingAnimal);

        AnimalOutDTO animalOutDTO = modelMapper.map(existingAnimal, AnimalOutDTO.class);
        return animalOutDTO;
    }
    // endregion

    // region PATCH request
    public AnimalOutDTO returnAnimal(long animalId) {
        Animal existingAnimal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));

        if (!existingAnimal.isAdopted()) {
            throw new NoChangeException("Este animal todavía está en adopción, no es posible devolverlo.");
        }

        existingAnimal.setAdopted(false);
        animalRepository.save(existingAnimal);

        AnimalOutDTO animalOutDTO = modelMapper.map(existingAnimal, AnimalOutDTO.class);
        return animalOutDTO;
    }
    // endregion
}
