package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.ErrorResponse;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;

    // region GET requests
    @GetMapping("/location/{locationId}")
    public Location getLocation(@PathVariable long locationId) throws ResourceNotFoundException {
        Optional<Location> optionalLocation = locationService.findById(locationId);
        return optionalLocation.orElseThrow(()->new ResourceNotFoundException(locationId));
    }
    @GetMapping("/locations")
    public List<Location> findAll(@RequestParam(defaultValue = "") String city, @RequestParam(defaultValue = "0") int zipCode){
        if(!city.isEmpty() && zipCode == 0){
            return locationService.findByCity(city);
        }
        else if(city.isEmpty() && zipCode != 0){
            return locationService.findByZipCode(zipCode);
        }
        else if(!city.isEmpty() && zipCode != 0){
            return locationService.findByCityAndZipCode(city, zipCode);
        }
        return locationService.getLocations();
    }
    @GetMapping("/location/{locationId}/animals")
    public List<Animal> getLocationAnimals(@PathVariable long locationId, @RequestParam(defaultValue = "") String species, @RequestParam(defaultValue = "0") int age, @RequestParam(defaultValue = "") String size) throws ResourceNotFoundException {
        if(!species.isEmpty() && age == 0 && size.isEmpty()){
            return locationService.findAnimalsBySpecies(locationId, species);
        }
        else if(species.isEmpty() && age != 0 && size.isEmpty()){
            return locationService.findAnimalsByAge(locationId, age);
        }
        else if(species.isEmpty() && age == 0 && !size.isEmpty()){
            return locationService.findAnimalsBySize(locationId, size);
        }
        else if(!species.isEmpty() && age != 0 && size.isEmpty()){
            return locationService.findAnimalsBySpeciesAndAge(locationId, species, age);
        }
        else if(!species.isEmpty() && age == 0 && !size.isEmpty()){
            return locationService.findAnimalsBySpeciesAndSize(locationId, species, size);
        }
        else if(species.isEmpty() && age != 0 && !size.isEmpty()){
            return locationService.findAnimalsByAgeAndSize(locationId, age, size);
        }
        else if(!species.isEmpty() && age != 0 && !size.isEmpty()){
            return locationService.findAnimalsBySpeciesAndAgeAndSize(locationId, species, age, size);
        }
        return locationService.getAnimalsByLocation(locationId);
    }
    // endregion

    // region POST request
    @PostMapping("/locations")
    public void saveLocation(@RequestBody Location location){
        locationService.saveLocation(location);
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

    //region EXCEPTION HANDLER
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException resNotFoundEx){
        ErrorResponse errorResponse = new ErrorResponse(404, resNotFoundEx.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    // endregion
}
