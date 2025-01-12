package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;

    // region GET requests
    @GetMapping("/location/{locationId}")
    public Location getLocation(@PathVariable long locationId) throws ResourceNotFoundException {
        return locationService.findById(locationId).orElseThrow(() -> new ResourceNotFoundException(locationId));
    }
    @GetMapping("/locations")
    public List<Location> findAll(@RequestParam(defaultValue = "") String city, @RequestParam(defaultValue = "0") String zipCode){
        if(!city.isEmpty() && zipCode.isEmpty()){
            return locationService.findByCity(city);
        }
        else if(city.isEmpty() && !zipCode.isEmpty()){
            return locationService.findByZipCode(zipCode);
        }
        else if(!city.isEmpty() && !zipCode.isEmpty()){
            return locationService.findByCityAndZipCode(city, zipCode);
        }
        return locationService.getLocations();
    }
    @GetMapping("/location/{locationId}/animals")
    public List<Animal> getAnimalsInLocation(@PathVariable long id) {
        return locationService.getAnimalsInLocation(id);
    }
    // endregion

    // region POST request
    @PostMapping("/locations")
    public void saveLocation(@RequestBody Location location){
        locationService.saveLocation(location);
    }
    @PostMapping("/location/{locationId}/animals")
    public void saveAnimalByLocation(@RequestBody Animal animal, @PathVariable long locationId){
        locationService.saveAnimalByLocation(animal, locationId);
    }
    // endregion

    // region DELETE request
    @DeleteMapping("/location/{locationId}")
    public void removeLocation(@PathVariable long locationId){
        locationService.removeLocation(locationId);
    }
    // endregion

    // region PUT request
    @PutMapping("/location/{locationId}")
    public void modifyLocation(@RequestBody Location location, @PathVariable long locationId){
        locationService.modifyLocation(location, locationId);
    }
    // endregion
}
