package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.repository.AnimalRepository;
import com.svalero.protectoraAnimales.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AnimalRepository animalRepository;

    // region GET requests
    public List<Location> getLocations(){
        return locationRepository.findAll();
    }
    public Optional<Location> findById(long locationId){
        return locationRepository.findById(locationId);
    }

    // Para "/locations":
    public List<Location> findByCity(String city){
        return locationRepository.findByCity(city);
    }

    public List<Location> findByZipCode(int zipCode){
        return locationRepository.findByZipCode(zipCode);
    }
    public List<Location> findByCityAndZipCode(String city, int zipCode){
        return locationRepository.findByCityAndZipCode(city, zipCode);
    }

    // Para "/location/{locationId}/animals":
    public List<Animal> getAnimalsByLocation(long locationId){
        return locationRepository.findByLocation(locationId);
    }
    public List<Animal> findAnimalsBySpecies(long locationId, String species){
        return locationRepository.findByLocationAndSpecies(locationId, species);
    }
    public List<Animal> findAnimalsByAge(long locationId, int age){
        return locationRepository.findByLocationAndAge(locationId, age);
    }
    public List<Animal> findAnimalsBySize(long locationId, String size){
        return locationRepository.findByLocationAndSize(locationId, size);
    }
    public List<Animal> findAnimalsBySpeciesAndAge(long locationId, String species, int age){
        return locationRepository.findByLocationAndSpeciesAndAge(locationId, species, age);
    }
    public List<Animal> findAnimalsBySpeciesAndSize(long locationId, String species, String size){
        return locationRepository.findByLocationAndSpeciesAndSize(locationId, species, size);
    }
    public List<Animal> findAnimalsByAgeAndSize(long locationId, int age, String size){
        return locationRepository.findByLocationAndAgeAndSize(locationId, age, size);
    }
    public List<Animal> findAnimalsBySpeciesAndAgeAndSize(long locationId, String species, int age, String size){
        return locationRepository.findByLocationAndSpeciesAndAgeAndSize(locationId, species, age, size);
    }
    // endregion

    // region POST request
    public void saveLocation(Location location){
        locationRepository.save(location);
    }
    public void saveAnimalByLocation(Animal animal, long locationId){
        Optional<Location> location = findById(locationId);
        if(location.isPresent()){
            animal.setLocation(location.get());
            animalRepository.save(animal);
        }
    }
    // endregion

    // region DELETE request
    public void removeLocation(long locationId){
        locationRepository.deleteById(locationId);
    }

    // endregion

    // region PUT request
    public void modifyLocation(Location newLocation, long locationId){
        Optional<Location> location = locationRepository.findById(locationId);

        if(location.isPresent()){
            Location existingLocation = location.get();

            existingLocation.setMainSite(newLocation.isMainSite());
            existingLocation.setAddress(newLocation.getAddress());
            existingLocation.setZipCode(newLocation.getZipCode());
            existingLocation.setCity(newLocation.getCity());
            existingLocation.setDescription(newLocation.getDescription());

            locationRepository.save(existingLocation);
        }
    }
    // endregion
}
