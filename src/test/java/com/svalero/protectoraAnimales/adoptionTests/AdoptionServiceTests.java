package com.svalero.protectoraAnimales.adoptionTests;

import com.svalero.protectoraAnimales.domain.Adoption;
import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.domain.dto.adoption.AdoptionChangePickUpInDTO;
import com.svalero.protectoraAnimales.domain.dto.adoption.AdoptionInDTO;
import com.svalero.protectoraAnimales.domain.dto.adoption.AdoptionOutDTO;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.AdoptionRepository;
import com.svalero.protectoraAnimales.repository.AnimalRepository;
import com.svalero.protectoraAnimales.repository.UserRepository;
import com.svalero.protectoraAnimales.service.AdoptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AdoptionServiceTests {

    @InjectMocks
    private AdoptionService adoptionService;

    @Mock
    private AdoptionRepository adoptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ModelMapper modelMapper;

    //region GET

    @Test
    public void testGetAdoptionById() {
        User user = new User(1, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(3, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f,"Afable y dormilón.", null, location, null);
        Adoption adoption = new Adoption(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal, user);

        when(adoptionRepository.findById(adoption.getId())).thenReturn(Optional.of(adoption));

        Adoption result = adoptionService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Rocky", result.getAnimal().getName());

        verify(adoptionRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAdoptionByIdNotFound() {
        long adoptionId = 8;

        when(adoptionRepository.findById(adoptionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adoptionService.findById(adoptionId));

        verify(adoptionRepository, times(1)).findById(adoptionId);
    }

    @Test
    public void testGetAdoptions() {
        User user = new User(1, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(3, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f,"Afable y dormilón.", null, location, null);
        Adoption adoption = new Adoption(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal, user);
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(),location.getId(), location.getCity(),user.getId(), user.getUsername(), user.getEmail());

        List<Adoption> adoptionList = List.of(adoption);
        List<AdoptionOutDTO> adoptionOutDTOList = List.of(adoptionOutDTO);

        when(adoptionRepository.findAll()).thenReturn(adoptionList);
        when(modelMapper.map(adoptionList, new TypeToken<List<AdoptionOutDTO>>(){}.getType())).thenReturn(adoptionOutDTOList);

        List<AdoptionOutDTO> result = adoptionService.getAdoptions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("laMorena", result.get(0).getUserUsername());

        verify(adoptionRepository, times(1)).findAll();
    }

    @Test
    public void testGetAdoptionsByAdoptionDate() {
        User user = new User(1, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(3, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f,"Afable y dormilón.", null, location, null);
        Adoption adoption = new Adoption(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal, user);
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), user.getId(), user.getUsername(), user.getEmail());

        LocalDate adoptionDate = LocalDate.of(2025, 4, 2);
        List<Adoption> adoptionList = List.of(adoption);
        List<AdoptionOutDTO> adoptionOutDTOList = List.of(adoptionOutDTO);

        when(adoptionRepository.findByAdoptionDate(adoptionDate)).thenReturn(adoptionList);
        when(modelMapper.map(adoptionList, new TypeToken<List<AdoptionOutDTO>>(){}.getType())).thenReturn(adoptionOutDTOList);

        List<AdoptionOutDTO> result = adoptionService.findByAdoptionDate(adoptionDate);

        assertEquals(1, result.size());
        assertEquals(adoptionDate, result.get(0).getAdoptionDate());

        verify(adoptionRepository, times(1)).findByAdoptionDate(adoptionDate);
    }

    @Test
    public void testGetAdoptionsByUserId() {
        User user = new User(1, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(3, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f,"Afable y dormilón.", null, location, null);
        Adoption adoption = new Adoption(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal, user);
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), user.getId(), user.getUsername(), user.getEmail());

        List<Adoption> adoptionList = List.of(adoption);
        List<AdoptionOutDTO> adoptionOutDTOList = List.of(adoptionOutDTO);

        when(adoptionRepository.findByUserId(user.getId())).thenReturn(adoptionList);
        when(modelMapper.map(adoptionList, new TypeToken<List<AdoptionOutDTO>>(){}.getType())).thenReturn(adoptionOutDTOList);

        List<AdoptionOutDTO> result = adoptionService.findByUserId(user.getId());

        assertEquals(1, result.size());
        assertEquals(user.getUsername(), result.get(0).getUserUsername());

        verify(adoptionRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    public void testGetAdoptionsByAnimalId() {
        User user = new User(1, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(3, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f, "Afable y dormilón.", null, location, null);
        Adoption adoption = new Adoption(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal, user);
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), user.getId(), user.getUsername(), user.getEmail());

        List<Adoption> adoptionList = List.of(adoption);
        List<AdoptionOutDTO> adoptionOutDTOList = List.of(adoptionOutDTO);

        when(adoptionRepository.findByAnimalId(animal.getId())).thenReturn(adoptionList);
        when(modelMapper.map(adoptionList, new TypeToken<List<AdoptionOutDTO>>(){}.getType())).thenReturn(adoptionOutDTOList);

        List<AdoptionOutDTO> result = adoptionService.findByAnimalId(animal.getId());

        assertEquals(1, result.size());
        assertEquals(animal.getName(), result.get(0).getAnimalName());

        verify(adoptionRepository, times(1)).findByAnimalId(animal.getId());
    }

    @Test
    public void testGetAdoptionsByAdoptionDateAndUserId() {
        User user = new User(1, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(3, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f, "Afable y dormilón.", null, location, null);
        Adoption adoption = new Adoption(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal, user);
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), user.getId(), user.getUsername(), user.getEmail());

        LocalDate adoptionDate = LocalDate.of(2025, 4, 2);
        List<Adoption> adoptionList = List.of(adoption);
        List<AdoptionOutDTO> adoptionOutDTOList = List.of(adoptionOutDTO);

        when(adoptionRepository.findByAdoptionDateAndUserId(adoptionDate, user.getId())).thenReturn(adoptionList);
        when(modelMapper.map(adoptionList, new TypeToken<List<AdoptionOutDTO>>(){}.getType())).thenReturn(adoptionOutDTOList);

        List<AdoptionOutDTO> result = adoptionService.findByAdoptionDateAndUserId(adoptionDate, user.getId());

        assertEquals(1, result.size());
        assertEquals(adoptionDate, result.get(0).getAdoptionDate());
        assertEquals(user.getId(), result.get(0).getUserId());

        verify(adoptionRepository, times(1)).findByAdoptionDateAndUserId(adoptionDate, user.getId());
    }

    @Test
    public void testGetAdoptionsByAdoptionDateAndAnimalId() {
        User user = new User(1, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(3, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f, "Afable y dormilón.", null, location, null);
        Adoption adoption = new Adoption(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal, user);
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), user.getId(), user.getUsername(), user.getEmail());

        LocalDate adoptionDate = LocalDate.of(2025, 4, 2);
        List<Adoption> adoptionList = List.of(adoption);
        List<AdoptionOutDTO> adoptionOutDTOList = List.of(adoptionOutDTO);

        when(adoptionRepository.findByAdoptionDateAndAnimalId(adoptionDate, animal.getId())).thenReturn(adoptionList);
        when(modelMapper.map(adoptionList, new TypeToken<List<AdoptionOutDTO>>(){}.getType())).thenReturn(adoptionOutDTOList);

        List<AdoptionOutDTO> result = adoptionService.findByAdoptionDateAndAnimalId(adoptionDate, animal.getId());

        assertEquals(1, result.size());
        assertEquals(animal.getId(), result.get(0).getAnimalId());
        assertEquals(adoptionDate, result.get(0).getAdoptionDate());

        verify(adoptionRepository, times(1)).findByAdoptionDateAndAnimalId(adoptionDate, animal.getId());
    }

    @Test
    public void testGetAdoptionsByAnimalIdAndUserId() {
        User user = new User(1, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(3, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f, "Afable y dormilón.", null, location, null);
        Adoption adoption = new Adoption(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal, user);
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), user.getId(), user.getUsername(), user.getEmail());

        List<Adoption> adoptionList = List.of(adoption);
        List<AdoptionOutDTO> adoptionOutDTOList = List.of(adoptionOutDTO);

        when(adoptionRepository.findByAnimalIdAndUserId(animal.getId(), user.getId())).thenReturn(adoptionList);
        when(modelMapper.map(adoptionList, new TypeToken<List<AdoptionOutDTO>>(){}.getType())).thenReturn(adoptionOutDTOList);

        List<AdoptionOutDTO> result = adoptionService.findByAnimalIdAndUserId(animal.getId(), user.getId());

        assertEquals(1, result.size());
        assertEquals(animal.getId(), result.get(0).getAnimalId());
        assertEquals(user.getId(), result.get(0).getUserId());

        verify(adoptionRepository, times(1)).findByAnimalIdAndUserId(animal.getId(), user.getId());
    }

    @Test
    public void testGetAdoptionsByAdoptionDateAndAnimalIdAndUserId() {
        LocalDate adoptionDate = LocalDate.of(2025, 4, 2);
        long animalId = 3;
        long userId = 1;

        User user = new User(userId, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(animalId, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f, "Afable y dormilón.", null, location, null);
        Adoption adoption = new Adoption(1L, adoptionDate, false, LocalDate.now().plusDays(3), "12:20", animal, user);

        List<Adoption> adoptionList = List.of(adoption);
        List<AdoptionOutDTO> adoptionOutDTOList = List.of(new AdoptionOutDTO(1L, adoptionDate, false, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), user.getId(), user.getUsername(), user.getEmail()));

        when(adoptionRepository.findByAdoptionDateAndAnimalIdAndUserId(adoptionDate, animalId, userId)).thenReturn(adoptionList);
        when(modelMapper.map(adoptionList, new TypeToken<List<AdoptionOutDTO>>(){}.getType())).thenReturn(adoptionOutDTOList);

        List<AdoptionOutDTO> result = adoptionService.findByAdoptionDateAndAnimalIdAndUserId(adoptionDate, animalId, userId);

        assertEquals(1, result.size());
        assertEquals(animalId, result.get(0).getAnimalId());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(adoptionDate, result.get(0).getAdoptionDate());

        verify(adoptionRepository, times(1)).findByAdoptionDateAndAnimalIdAndUserId(adoptionDate, animalId, userId);
    }

    @Test
    public void testGetAdoptionsPickUpsInNextTwoWeeks() {
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksFromNow = today.plusWeeks(2);

        User user = new User(1, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(3, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f, "Afable y dormilón.", null, location, null);
        Adoption adoption = new Adoption(1, LocalDate.of(2025, 4, 2), false, today.plusDays(10), "12:20", animal, user);

        List<Adoption> adoptionList = List.of(adoption);
        List<AdoptionOutDTO> adoptionOutDTOList = List.of(new AdoptionOutDTO(1L, adoption.getAdoptionDate(), false, today.plusDays(10), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), user.getId(), user.getUsername(), user.getEmail()));

        when(adoptionRepository.findPickUpsInNextTwoWeeks(today, twoWeeksFromNow)).thenReturn(adoptionList);
        when(modelMapper.map(adoptionList, new TypeToken<List<AdoptionOutDTO>>(){}.getType())).thenReturn(adoptionOutDTOList);

        List<AdoptionOutDTO> result = adoptionService.findPickUpsInNextTwoWeeks();

        assertEquals(1, result.size());

        verify(adoptionRepository, times(1)).findPickUpsInNextTwoWeeks(today, twoWeeksFromNow);
    }

    //endregion

    //region POST

    @Test
    public void testSaveAdoption() {
        long userId = 1;
        long animalId = 2;

        User user = new User(userId, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(animalId, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", false, false, 214.25f, "Afable y dormilón.", null, location, null);

        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(false, LocalDate.now().plusDays(5), "15:00");
        Adoption adoption = new Adoption(1, LocalDate.of(2025, 4, 2), false, LocalDate.now().plusDays(3), "12:20", animal, user);
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.now(), false, adoptionInDTO.getPickUpDate(), adoptionInDTO.getPickUpTime(), animalId, animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), userId, user.getUsername(), user.getEmail());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(modelMapper.map(adoptionInDTO, Adoption.class)).thenReturn(adoption);
        when(modelMapper.map(adoption, AdoptionOutDTO.class)).thenReturn(adoptionOutDTO);
        when(adoptionRepository.save(adoption)).thenReturn(adoption);
        when(animalRepository.save(animal)).thenReturn(animal);

        AdoptionOutDTO result = adoptionService.saveAdoption(userId, animalId, adoptionInDTO);

        assertEquals(userId, result.getUserId());
        assertEquals(animalId, result.getAnimalId());
        assertEquals(adoptionInDTO.getPickUpDate(), result.getPickUpDate());

        verify(userRepository, times(1)).findById(userId);
        verify(animalRepository, times(1)).findById(animalId);
        verify(adoptionRepository, times(1)).save(adoption);
        verify(animalRepository, times(1)).save(animal);
    }

    @Test
    public void testSaveAdoptionWhenUserNotFound() {
        long userId = 1;
        long animalId = 2;
        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(false, LocalDate.now().plusDays(5), "15:00");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adoptionService.saveAdoption(userId, animalId, adoptionInDTO));

        verify(userRepository, times(1)).findById(userId);
        verify(animalRepository, never()).findById(anyLong());
        verify(adoptionRepository, never()).save(any(Adoption.class));
    }

    @Test
    public void testSaveAdoptionWhenAnimalAlreadyAdopted() {
        long userId = 1;
        long animalId = 2;
        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(false, LocalDate.now().plusDays(5), "15:00");

        User user = new User(userId, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(animalId, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", false, true, 214.25f, "Afable y dormilón.", null, location, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(modelMapper.map(adoptionInDTO, Adoption.class)).thenReturn(new Adoption());

        assertThrows(IllegalStateException.class, () -> adoptionService.saveAdoption(userId, animalId, adoptionInDTO));

        verify(userRepository, times(1)).findById(userId);
        verify(animalRepository, times(1)).findById(animalId);
        verify(adoptionRepository, never()).save(any(Adoption.class));
    }

    //endregion

    //region DELETE

    @Test
    public void testRemoveAdoption() {
        long adoptionId = 5;

        when(adoptionRepository.existsById(adoptionId)).thenReturn(true);

        adoptionService.removeAdoption(adoptionId);

        verify(adoptionRepository, times(1)).deleteById(adoptionId);
    }

    @Test
    public void testRemoveAdoptionWhenNotFound() {
        long adoptionId = 10;

        when(adoptionRepository.existsById(adoptionId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adoptionService.removeAdoption(adoptionId));

        verify(adoptionRepository, never()).deleteById(anyLong());
    }

    //endregion

    //region PUT

    @Test
    public void testModifyAdoption() {
        long adoptionId = 1;
        long animalId = 2;
        long userId = 3;

        User user = new User(userId, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(animalId, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", false, false, 214.25f, "Afable y dormilón.", null, location, null);

        Adoption existingAdoption = new Adoption(adoptionId, LocalDate.now(), false, LocalDate.now().plusDays(3), "12:20", animal, user);
        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(true, LocalDate.now().plusDays(5), "15:00");
        Adoption adoptionMapped = new Adoption(0, null, true, adoptionInDTO.getPickUpDate(), adoptionInDTO.getPickUpTime(), null, null);
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(adoptionId, LocalDate.now(), true, adoptionInDTO.getPickUpDate(), adoptionInDTO.getPickUpTime(), animalId, animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), userId, user.getUsername(), user.getEmail());

        when(adoptionRepository.findById(adoptionId)).thenReturn(Optional.of(existingAdoption));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        when(modelMapper.map(adoptionInDTO, Adoption.class)).thenReturn(adoptionMapped);
        when(modelMapper.map(existingAdoption, AdoptionOutDTO.class)).thenReturn(adoptionOutDTO);
        when(adoptionRepository.save(existingAdoption)).thenReturn(existingAdoption);

        AdoptionOutDTO result = adoptionService.modifyAdoption(adoptionId, animalId, userId, adoptionInDTO);

        assertEquals(1, result.getId());
        assertEquals(3, result.getUserId());
        assertEquals(2, result.getAnimalId());
        assertEquals(adoptionInDTO.getPickUpDate(), result.getPickUpDate());
        assertEquals("15:00", result.getPickUpTime());
        assertTrue(result.isTakeAccessories());

        verify(adoptionRepository, times(1)).save(existingAdoption);
    }

    @Test
    public void testModifyAdoptionWhenAdoptionNotFound() {
        long adoptionId = 8;
        long animalId = 1;
        long userId = 1;
        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(true, LocalDate.now().plusDays(5), "15:00");

        when(adoptionRepository.findById(adoptionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adoptionService.modifyAdoption(adoptionId, animalId, userId, adoptionInDTO));

        verify(adoptionRepository, times(1)).findById(adoptionId);
        verify(adoptionRepository, never()).save(any());
    }

    @Test
    public void testModifyAdoptionWhenUserNotFound() {
        long adoptionId = 1;
        long animalId = 2;
        long userId = 8;
        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(true, LocalDate.now().plusDays(5), "15:00");
        Adoption existingAdoption = new Adoption(adoptionId, LocalDate.now(), false, LocalDate.now().plusDays(3), "12:20", null, null);

        when(adoptionRepository.findById(adoptionId)).thenReturn(Optional.of(existingAdoption));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adoptionService.modifyAdoption(adoptionId, animalId, userId, adoptionInDTO));

        verify(adoptionRepository, times(1)).findById(adoptionId);
        verify(userRepository, times(1)).findById(userId);
        verify(adoptionRepository, never()).save(any());
    }

    @Test
    public void testModifyAdoptionWhenAnimalNotFound() {
        long adoptionId = 1;
        long animalId = 8;
        long userId = 2;
        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(true, LocalDate.now().plusDays(5), "15:00");

        Adoption existingAdoption = new Adoption(adoptionId, LocalDate.now(), false, LocalDate.now().plusDays(3), "12:20", null, null);
        User user = new User(userId, "userX", "User", "X", LocalDate.of(1980, 1, 1), "userx@mail.com", List.of(), List.of());

        when(adoptionRepository.findById(adoptionId)).thenReturn(Optional.of(existingAdoption));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(animalRepository.findById(animalId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adoptionService.modifyAdoption(adoptionId, animalId, userId, adoptionInDTO));

        verify(adoptionRepository, times(1)).findById(adoptionId);
        verify(userRepository, times(1)).findById(userId);
        verify(animalRepository, times(1)).findById(animalId);
        verify(adoptionRepository, never()).save(any());
    }

    //endregion

    //region PATCH

    @Test
    public void testChangePickUpDataSuccess() {
        long adoptionId = 1;
        AdoptionChangePickUpInDTO pickUpData = new AdoptionChangePickUpInDTO(LocalDate.now().plusDays(7), "16:30");

        User user = new User(3, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción", 15d, 16d, new ArrayList<>());
        Animal animal = new Animal(2, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", false, false, 214.25f, "Afable y dormilón.", null, location, null);

        Adoption existingAdoption = new Adoption(adoptionId, LocalDate.now(), false, LocalDate.now().plusDays(3), "12:20", animal, user);
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(adoptionId, LocalDate.now(), false, pickUpData.getPickUpDate(), pickUpData.getPickUpTime(), animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), location.getId(), location.getCity(), user.getId(), user.getUsername(), user.getEmail());

        when(adoptionRepository.findById(adoptionId)).thenReturn(Optional.of(existingAdoption));
        when(adoptionRepository.save(existingAdoption)).thenReturn(existingAdoption);
        when(modelMapper.map(existingAdoption, AdoptionOutDTO.class)).thenReturn(adoptionOutDTO);

        AdoptionOutDTO result = adoptionService.changePickUpData(adoptionId, pickUpData);

        assertEquals(adoptionId, result.getId());
        assertEquals(pickUpData.getPickUpDate(), result.getPickUpDate());
        assertEquals("16:30", result.getPickUpTime());

        verify(adoptionRepository, times(1)).findById(adoptionId);
        verify(adoptionRepository, times(1)).save(existingAdoption);
    }

    @Test
    public void testChangePickUpDataAdoptionNotFound() {
        long adoptionId = 8;
        AdoptionChangePickUpInDTO pickUpData = new AdoptionChangePickUpInDTO(LocalDate.now().plusDays(7), "16:30");

        when(adoptionRepository.findById(adoptionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adoptionService.changePickUpData(adoptionId, pickUpData));

        verify(adoptionRepository, times(1)).findById(adoptionId);
        verify(adoptionRepository, never()).save(any());
    }

    //endregion
}
