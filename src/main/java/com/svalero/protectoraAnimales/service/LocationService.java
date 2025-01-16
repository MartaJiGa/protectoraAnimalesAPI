package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.AnimalRepository;
import com.svalero.protectoraAnimales.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AnimalRepository animalRepository;

    // region GET requests
    public Location findById(long locationId){
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."));
    }
    public List<Location> getLocations(){
        List<Location> locations = locationRepository.findAll();
        if (locations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron ubicaciones.");
        }
        return locations;
    }
    public List<Location> findByCity(String city){
        List<Location> locations = locationRepository.findByCity(city);
        if (locations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron ubicaciones en " + city);
        }
        return locations;
    }
    public List<Location> findByZipCode(String zipCode){
        List<Location> locations = locationRepository.findByZipCode(zipCode);
        if (locations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron ubicaciones con el código postal " + zipCode);
        }
        return locations;
    }
    public List<Location> findByCityAndZipCode(String city, String zipCode){
        List<Location> locations = locationRepository.findByCityAndZipCode(city, zipCode);
        if (locations.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron ubicaciones en la ciudad " + city + " con código postal " + zipCode);
        }
        return locations;
    }
    public List<Animal> findAnimalsByLocationId(long locationId){
        List<Animal> animals = animalRepository.findByLocationId(locationId);
        if (animals.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron animales para esa ubicación.");
        }
        return animals;
    }
    // endregion

    // region POST request
    public Location saveLocation(Location location){
        return locationRepository.save(location);
    }
    // endregion

    // region DELETE request
    public void removeLocation(long locationId){
        if (!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada.");
        }
        locationRepository.deleteById(locationId);
    }
    // endregion

    // region PUT request
    public Location modifyLocation(Location newLocation, long locationId){
        Location existingLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."));

        existingLocation.setMainSite(newLocation.isMainSite());
        existingLocation.setAddress(newLocation.getAddress());
        existingLocation.setZipCode(newLocation.getZipCode());
        existingLocation.setCity(newLocation.getCity());
        existingLocation.setDescription(newLocation.getDescription());

        return locationRepository.save(existingLocation);
    }
    // endregion
}
