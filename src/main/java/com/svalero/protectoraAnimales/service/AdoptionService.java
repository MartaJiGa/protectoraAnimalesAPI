package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.domain.Adoption;
import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.domain.dto.adoption.AdoptionChangePickUpInDTO;
import com.svalero.protectoraAnimales.domain.dto.adoption.AdoptionInDTO;
import com.svalero.protectoraAnimales.domain.dto.adoption.AdoptionOutDTO;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.AdoptionRepository;
import com.svalero.protectoraAnimales.repository.AnimalRepository;
import com.svalero.protectoraAnimales.repository.UserRepository;
import org.modelmapper.ModelMapper;
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
    @Autowired
    private ModelMapper modelMapper;

    // region GET requests
    public Adoption findById(long adoptionId){
        return adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada."));
    }
    public List<AdoptionOutDTO> getAdoptions(){
        List<Adoption> adoptions = adoptionRepository.findAll();
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones.");
        }

        List<AdoptionOutDTO> adoptionOutDTOS = modelMapper.map(adoptions, new TypeToken<List<AdoptionOutDTO>>(){}.getType());
        return adoptionOutDTOS;
    }
    public List<AdoptionOutDTO> findByAdoptionDate(LocalDate adoptionDate){
        List<Adoption> adoptions = adoptionRepository.findByAdoptionDate(adoptionDate);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones en la fecha " + adoptionDate);
        }

        List<AdoptionOutDTO> adoptionOutDTOS = modelMapper.map(adoptions, new TypeToken<List<AdoptionOutDTO>>(){}.getType());
        return adoptionOutDTOS;
    }
    public List<AdoptionOutDTO> findByUserId(long userId){
        List<Adoption> adoptions = adoptionRepository.findByUserId(userId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones con ID de usuario " + userId);
        }

        List<AdoptionOutDTO> adoptionOutDTOS = modelMapper.map(adoptions, new TypeToken<List<AdoptionOutDTO>>(){}.getType());
        return adoptionOutDTOS;
    }
    public List<AdoptionOutDTO> findByAnimalId(long animalId) {
        List<Adoption> adoptions = adoptionRepository.findByAnimalId(animalId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones con ID de animal " + animalId);
        }

        List<AdoptionOutDTO> adoptionOutDTOS = modelMapper.map(adoptions, new TypeToken<List<AdoptionOutDTO>>(){}.getType());
        return adoptionOutDTOS;
    }
    public List<AdoptionOutDTO> findByAdoptionDateAndUserId(LocalDate adoptionDate, long userId) {
        List<Adoption> adoptions = adoptionRepository.findByAdoptionDateAndUserId(adoptionDate, userId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones con fecha " + adoptionDate + " e ID de usuario " + userId);
        }

        List<AdoptionOutDTO> adoptionOutDTOS = modelMapper.map(adoptions, new TypeToken<List<AdoptionOutDTO>>(){}.getType());
        return adoptionOutDTOS;
    }
    public List<AdoptionOutDTO> findByAdoptionDateAndAnimalId(LocalDate adoptionDate, long animalId) {
        List<Adoption> adoptions = adoptionRepository.findByAdoptionDateAndAnimalId(adoptionDate, animalId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones con fecha " + adoptionDate + " e ID de animal " + animalId);
        }

        List<AdoptionOutDTO> adoptionOutDTOS = modelMapper.map(adoptions, new TypeToken<List<AdoptionOutDTO>>(){}.getType());
        return adoptionOutDTOS;
    }
    public List<AdoptionOutDTO> findByAnimalIdAndUserId(long animalId, long userId) {
        List<Adoption> adoptions = adoptionRepository.findByAnimalIdAndUserId(animalId, userId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones de ID de animal " + animalId + " e ID de usuario " + userId);
        }

        List<AdoptionOutDTO> adoptionOutDTOS = modelMapper.map(adoptions, new TypeToken<List<AdoptionOutDTO>>(){}.getType());
        return adoptionOutDTOS;
    }
    public List<AdoptionOutDTO> findByAdoptionDateAndAnimalIdAndUserId(LocalDate adoptionDate, long animalId, long userId) {
        List<Adoption> adoptions = adoptionRepository.findByAdoptionDateAndAnimalIdAndUserId(adoptionDate, animalId, userId);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron adopciones con fecha " + adoptionDate + ", ID de animal " + animalId + " e ID de usuario " + userId);
        }

        List<AdoptionOutDTO> adoptionOutDTOS = modelMapper.map(adoptions, new TypeToken<List<AdoptionOutDTO>>(){}.getType());
        return adoptionOutDTOS;
    }
    public List<AdoptionOutDTO> findPickUpsInNextTwoWeeks(){
        LocalDate twoWeeksFromNow = LocalDate.now().plusWeeks(2);
        List<Adoption> adoptions = adoptionRepository.findPickUpsInNextTwoWeeks(LocalDate.now(), twoWeeksFromNow);
        if (adoptions.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron fechas de recogida de adopciones en las dos próximas semanas.");
        }

        List<AdoptionOutDTO> adoptionOutDTOS = modelMapper.map(adoptions, new TypeToken<List<AdoptionOutDTO>>(){}.getType());
        return adoptionOutDTOS;
    }
    // endregion

    // region POST request
    public AdoptionOutDTO saveAdoption(long userId, long animalId, AdoptionInDTO adoptionInDTO){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));

        Adoption adoption = modelMapper.map(adoptionInDTO, Adoption.class);

        if (animal.isAdopted()) {
            throw new IllegalStateException("Este animal ya ha sido adoptado.");
        }

        adoption.setUser(user);
        adoption.setAnimal(animal);
        adoption.setAdoptionDate(LocalDate.now());
        adoptionRepository.save(adoption);

        animal.setAdopted(true);
        animalRepository.save(animal);

        AdoptionOutDTO adoptionOutDTO = modelMapper.map(adoption, AdoptionOutDTO.class);
        return adoptionOutDTO;
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
    public AdoptionOutDTO modifyAdoption(long adoptionId, long animalId, long userId, AdoptionInDTO adoptionInDTO) {
        Adoption existingAdoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));

        Adoption adoption = modelMapper.map(adoptionInDTO, Adoption.class);

        existingAdoption.setTakeAccessories(adoption.isTakeAccessories());
        existingAdoption.setPickUpDate(adoption.getPickUpDate());
        existingAdoption.setPickUpTime(adoption.getPickUpTime());
        existingAdoption.setUser(user);
        existingAdoption.setAnimal(animal);

        adoptionRepository.save(existingAdoption);

        AdoptionOutDTO adoptionOutDTO = modelMapper.map(existingAdoption, AdoptionOutDTO.class);
        return adoptionOutDTO;
    }
    // endregion

    // region PATCH request
    public AdoptionOutDTO changePickUpData(long adoptionId, AdoptionChangePickUpInDTO adoptionPickUpData) {
        Adoption existingAdoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada."));

        existingAdoption.setPickUpDate(adoptionPickUpData.getPickUpDate());
        existingAdoption.setPickUpTime(adoptionPickUpData.getPickUpTime());

        adoptionRepository.save(existingAdoption);

        AdoptionOutDTO adoptionOutDTO = modelMapper.map(existingAdoption, AdoptionOutDTO.class);
        return adoptionOutDTO;
    }
    // endregion
}
