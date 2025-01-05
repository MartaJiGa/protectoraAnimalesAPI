package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
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
    public List<Animal> getAnimalsInLocation(long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        return location.getAnimals();
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
