package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;

    // region GET requests
    @GetMapping("/location/{locationId}")
    public Location getLocation(@PathVariable long locationId) {
        return locationService.findById(locationId);
    }
    @GetMapping("/locations")
    public ResponseEntity<List<Location>> findAll(@RequestParam(defaultValue = "") String city, @RequestParam(defaultValue = "") String zipCode){
        List<Location> locations;

        if(!city.isEmpty() && zipCode.isEmpty()){
            locations = locationService.findByCity(city);
        }
        else if(city.isEmpty() && !zipCode.isEmpty()){
            locations = locationService.findByZipCode(zipCode);
        }
        else if(!city.isEmpty() && !zipCode.isEmpty()){
            locations = locationService.findByCityAndZipCode(city, zipCode);
        }
        else{
            locations = locationService.getLocations();
        }

        return ResponseEntity.ok(locations);
    }
    @GetMapping("/location/{locationId}/animals")
    public List<Animal> getAnimalsInLocation(@PathVariable long locationId) {
        return locationService.findAnimalsByLocationId(locationId);
    }
    // endregion

    // region POST request
    @PostMapping("/locations")
    public ResponseEntity<Location> saveLocation(@Valid @RequestBody Location location){
        Location savedLocation = locationService.saveLocation(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLocation);
    }
    // endregion

    // region DELETE request
    @DeleteMapping("/location/{locationId}")
    public ResponseEntity<Void> removeLocation(@PathVariable long locationId){
        locationService.removeLocation(locationId);
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region PUT request
    @PutMapping("/location/{locationId}")
    public ResponseEntity<Location> modifyLocation(@Valid @RequestBody Location location, @PathVariable long locationId){
        locationService.modifyLocation(location, locationId);
        return ResponseEntity.ok(location);
    }
    // endregion
}
