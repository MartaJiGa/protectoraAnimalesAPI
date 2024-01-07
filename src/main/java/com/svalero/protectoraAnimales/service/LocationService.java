package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    // region GET requests
    public List<Location> getLocations(){
        return locationRepository.findAll();
    }

    public List<Location> getLocationByCity(String city){
        return locationRepository.findByCity(city);
    }

    public List<Location> getLocationByZipCode(int zipCode){
        return locationRepository.findByZipCode(zipCode);
    }

    public List<Location> getLocationByCityAndZipCode(String city, int zipCode){
        return locationRepository.findByCityAndZipCode(city, zipCode);
    }

    public Optional<Location> getLocationById(long locationId){
        return locationRepository.findById(locationId);
    }
    // endregion

    // region POST request
    public void saveLocation(Location location){
        locationRepository.save(location);
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
