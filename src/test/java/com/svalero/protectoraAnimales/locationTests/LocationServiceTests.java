package com.svalero.protectoraAnimales.locationTests;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalOutDTO;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.AnimalRepository;
import com.svalero.protectoraAnimales.repository.LocationRepository;
import com.svalero.protectoraAnimales.service.LocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTests {

    @InjectMocks
    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ModelMapper modelMapper;

    //region GET

    @Test
    public void testFindLocationById() {
        long locationId = 1;
        Location mockLocation = new Location(locationId, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>());

        when(locationRepository.findById(locationId)).thenReturn(Optional.ofNullable(mockLocation));

        Location result = locationService.findById(locationId);

        assertEquals(locationId, result.getId());
        assertEquals("Zaragoza", result.getCity());
        assertEquals("50003", result.getZipCode());

        verify(locationRepository, times(1)).findById(locationId);
    }

    @Test
    public void testFindLocationWhenLocationIsNotFound() {
        long locationId = 3;

        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> locationService.findById(locationId));

        verify(locationRepository, times(1)).findById(locationId);
    }

    @Test
    public void testGetLocations() {
        List<Location> mockLocationList = List.of(
                new Location(1, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>()),
                new Location(4, false, "Rua dos Animais 45", "15003", "A Coruña", "Refugio cerrado temporalmente por reformas.", new ArrayList<>()),
                new Location(15, false, "Passeig dels Gossos S/N", "17002", "Girona", "Punto de adopción y vacunación en colaboración con veterinarias locales.", new ArrayList<>())
        );

        when(locationRepository.findAll()).thenReturn(mockLocationList);

        List<Location> result = locationService.getLocations();

        assertEquals(3, result.size());
        assertEquals("Zaragoza", result.get(0).getCity());
        assertEquals("Rua dos Animais 45", result.get(1).getAddress());
        assertEquals(false, result.get(2).isMainSite());

        verify(locationRepository, times(1)).findAll();
    }

    @Test
    public void testFindLocationsByCity() {
        String city = "Girona";

        List<Location> mockLocationList = List.of(
                new Location(15, false, "Passeig dels Gossos S/N", "17002", "Girona", "Punto de adopción y vacunación en colaboración con veterinarias locales.", new ArrayList<>())
        );

        when(locationRepository.findByCity(city)).thenReturn(mockLocationList);

        List<Location> result = locationService.findByCity(city);

        assertEquals(1, result.size());
        assertEquals("Girona", result.get(0).getCity());
        assertEquals("17002", result.get(0).getZipCode());

        verify(locationRepository, times(1)).findByCity(city);
    }

    @Test
    public void testFindLocationsByZipCode() {
        String zipCode = "50003";

        List<Location> mockLocations = List.of(
                new Location(1, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>()),
                new Location(6, true, "Camino de las aves", "50003", "Zaragoza", "Zona de acogida temporal", new ArrayList<>())
        );

        when(locationRepository.findByZipCode(zipCode)).thenReturn(mockLocations);

        List<Location> result = locationService.findByZipCode(zipCode);

        assertEquals(2, result.size());
        assertEquals("50003", result.get(0).getZipCode());
        assertEquals("Zaragoza", result.get(1).getCity());

        verify(locationRepository, times(1)).findByZipCode(zipCode);
    }

    @Test
    public void testFindLocationsByCityAndZipCode() {
        String city = "Zaragoza";
        String zipCode = "50003";

        List<Location> mockLocations = List.of(
                new Location(1, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>()),
                new Location(6, true, "Camino de las aves", "50003", "Zaragoza", "Zona de acogida temporal", new ArrayList<>())
        );

        when(locationRepository.findByCityAndZipCode(city, zipCode)).thenReturn(mockLocations);

        List<Location> result = locationService.findByCityAndZipCode(city, zipCode);

        assertEquals(2, result.size());
        assertEquals("Zaragoza", result.get(0).getCity());
        assertEquals("50003", result.get(1).getZipCode());

        verify(locationRepository, times(1)).findByCityAndZipCode(city, zipCode);
    }

    @Test
    public void testFindAnimalsByLocationId() {
        long locationId = 1;
        Location mockLocation = new Location(locationId, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>());
        List<Animal> mockAnimalList = List.of(
                new Animal(1, LocalDate.of(2023, 1, 10), "Luna", "Perro", 3, "Labrador", "Grande", true, false, 150.0f, "Muy amigable", mockLocation, null),
                new Animal(2, LocalDate.now(), "Milo", "Gato", 2, "Siamés", "Pequeño", false, true, 100.0f, "Cariñoso y tranquilo", mockLocation, null),
                new Animal(3, LocalDate.of(2024, 3, 5), "Kira", "Conejo", 1, "Enano", "Pequeño", true, false, 50.0f, "Le encanta saltar", mockLocation, null)
        );

        when(animalRepository.findByLocationId(locationId)).thenReturn(mockAnimalList);

        List<Animal> result = locationService.findAnimalsByLocationId(locationId);

        assertEquals(3, result.size());
        assertEquals("Luna", result.get(0).getName());
        assertEquals("Gato", result.get(1).getSpecies());

        verify(animalRepository, times(1)).findByLocationId(locationId);
    }

    @Test
    public void testFindAnimalsByLocationIdNotFound() {
        long locationId = 3;

        when(animalRepository.findByLocationId(locationId)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> locationService.findAnimalsByLocationId(locationId));

        verify(animalRepository, times(1)).findByLocationId(locationId);
    }

    //endregion

    //region POST
    //endregion

    //region DELETE
    //endregion

    //region PUT
    //endregion

    //region PATCH
    //endregion
}
