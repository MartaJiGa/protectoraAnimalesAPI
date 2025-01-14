package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.domain.Adoption;
import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.domain.dto.AnimalInDTO;
import com.svalero.protectoraAnimales.domain.dto.AnimalOutDTO;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.AdoptionRepository;
import com.svalero.protectoraAnimales.repository.AnimalRepository;
import com.svalero.protectoraAnimales.repository.UserRepository;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdoptionService {
    @Autowired
    private AdoptionRepository adoptionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AnimalRepository animalRepository;

    // region GET requests
    public Adoption findById(long adoptionId){
        return adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada."));
    }
    public List<Adoption> getAdoptions(){
        List<Adoption> adoptions = adoptionRepository.findAll();
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones.");
        }
        return adoptions;
    }
    public List<Adoption> findByAdoptionDate(LocalDate adoptionDate){
        List<Adoption> adoptions = adoptionRepository.findByAdoptionDate(adoptionDate);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones en la fecha " + adoptionDate);
        }
        return adoptions;
    }
    public List<Adoption> findByUserId(long userId){
        List<Adoption> adoptions = adoptionRepository.findByUserId(userId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones con ID de usuario " + userId);
        }
        return adoptions;
    }
    public List<Adoption> findByAnimalId(long animalId) {
        List<Adoption> adoptions = adoptionRepository.findByAnimalId(animalId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones con ID de animal " + animalId);
        }
        return adoptions;
    }
    public List<Adoption> findByAdoptionDateAndUserId(LocalDate adoptionDate, long userId) {
        List<Adoption> adoptions = adoptionRepository.findByAdoptionDateAndUserId(adoptionDate, userId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones con fecha " + adoptionDate + " e ID de usuario " + userId);
        }
        return adoptions;
    }
    public List<Adoption> findByAdoptionDateAndAnimalId(LocalDate adoptionDate, long animalId) {
        List<Adoption> adoptions = adoptionRepository.findByAdoptionDateAndAnimalId(adoptionDate, animalId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones con fecha " + adoptionDate + " e ID de animal " + animalId);
        }
        return adoptions;
    }
    public List<Adoption> findByAnimalIdAndUserId(long animalId, long userId) {
        List<Adoption> adoptions = adoptionRepository.findByAnimalIdAndUserId(animalId, userId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones de ID de animal " + animalId + " e ID de usuario " + userId);
        }
        return adoptions;
    }
    public List<Adoption> findByAdoptionDateAndAnimalIdAndUserId(LocalDate adoptionDate, long animalId, long userId) {
        List<Adoption> adoptions = adoptionRepository.findByAdoptionDateAndAnimalIdAndUserId(adoptionDate, animalId, userId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones con fecha " + adoptionDate + ", ID de animal " + animalId + " e ID de usuario " + userId);
        }
        return adoptions;
    }
    // endregion

    // region POST request
    public Adoption saveAnimal(long userId, long animalId, Adoption adoption){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));

        adoption.setUser(user);
        adoption.setAnimal(animal);
        adoption.setAdoptionDate(LocalDate.now());

        return adoptionRepository.save(adoption);
    }
    // endregion

    // region DELETE request
    public void removeAdoption(long adoptionId){
        if (!adoptionRepository.existsById(adoptionId)) {
            throw new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada.");
        }
        adoptionRepository.deleteById(adoptionId);
    }
    // endregion

    // region PUT request
    public Adoption modifyAdoption(Adoption newAdoption, long adoptionId) {
        Adoption existingAdoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada."));

        existingAdoption.setTakeAccessories(newAdoption.getTakeAccessories());
        existingAdoption.setPickUpDate(newAdoption.getPickUpDate());
        existingAdoption.setPickUpTime(newAdoption.getPickUpTime());

        return adoptionRepository.save(existingAdoption);
    }
    public Adoption modifyAdoptionAnimal(long animalId, long adoptionId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));

        Adoption adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada."));

        adoption.setAnimal(animal);
        return adoptionRepository.save(adoption);
    }
    public Adoption modifyAdoptionUser(long userId, long adoptionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        Adoption adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada."));

        adoption.setUser(user);
        return adoptionRepository.save(adoption);
    }
    // endregion
}
