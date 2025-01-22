package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.domain.dto.location.LocationInDTO;
import com.svalero.protectoraAnimales.service.LocationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;
    private final Logger logger = LoggerFactory.getLogger(LocationController.class);

    // region GET requests
    @GetMapping("/location/{locationId}")
    public Location getLocation(@PathVariable long locationId) {
        logger.info("BEGIN getLocation()");
        Location location = locationService.findById(locationId);
        logger.info("END getLocation()");
        return location;
    }
    @GetMapping("/locations")
    public ResponseEntity<List<Location>> findAllLocations(@RequestParam(defaultValue = "") String city,
                                                           @RequestParam(defaultValue = "") String zipCode){
        List<Location> locations;

        if(!city.isEmpty() && zipCode.isEmpty()){
            logger.info("BEGIN findAllLocations() -> ByCity");
            locations = locationService.findByCity(city);
            logger.info("END findAllLocations() -> ByCity");
        }
        else if(city.isEmpty() && !zipCode.isEmpty()){
            logger.info("BEGIN findAllLocations() -> ByZipCode");
            locations = locationService.findByZipCode(zipCode);
            logger.info("END findAllLocations() -> ByZipCode");
        }
        else if(!city.isEmpty() && !zipCode.isEmpty()){
            logger.info("BEGIN findAllLocations() -> ByCityAndZipCode");
            locations = locationService.findByCityAndZipCode(city, zipCode);
            logger.info("END findAllLocations() -> ByCityAndZipCode");
        }
        else{
            logger.info("BEGIN findAllLocations()");
            locations = locationService.getLocations();
            logger.info("END findAllLocations()");
        }

        return ResponseEntity.ok(locations);
    }
    @GetMapping("/location/{locationId}/animals")
    public List<Animal> getAnimalsInLocation(@PathVariable long locationId) {
        logger.info("BEGIN getAnimalsInLocation()");
        List<Animal> animals = locationService.findAnimalsByLocationId(locationId);
        logger.info("END getAnimalsInLocation()");
        return animals;
    }
    // endregion

    // region POST request
    @PostMapping("/locations")
    public ResponseEntity<Location> saveLocation(@Valid @RequestBody LocationInDTO location){
        logger.info("BEGIN saveLocation()");
        Location savedLocation = locationService.saveLocation(location);
        logger.info("END saveLocation()");
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLocation);
    }
    // endregion

    // region DELETE request
    @DeleteMapping("/location/{locationId}")
    public ResponseEntity<Void> removeLocation(@PathVariable long locationId){
        logger.info("BEGIN removeLocation()");
        locationService.removeLocation(locationId);
        logger.info("END removeLocation()");
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region PUT request
    @PutMapping("/location/{locationId}")
    public ResponseEntity<Location> modifyLocation(@Valid @RequestBody LocationInDTO location,
                                                   @PathVariable long locationId){
        logger.info("BEGIN modifyLocation()");
        Location returnedLocation = locationService.modifyLocation(location, locationId);
        logger.info("END modifyLocation()");
        return ResponseEntity.ok(returnedLocation);
    }
    // endregion

    // region PATCH request
    @PatchMapping("/location/{locationId}/mainSite")
    public ResponseEntity<Location> changeMainSite(@PathVariable long locationId){
        logger.info("BEGIN changeMainSite()");
        Location returnedLocation = locationService.changeMainSite(locationId);
        logger.info("END changeMainSite()");
        return ResponseEntity.ok(returnedLocation);
    }
}
