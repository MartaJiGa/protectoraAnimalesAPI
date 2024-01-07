package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;

    // region GET requests
    @GetMapping("/location/{locationId}")
    public Location getLocation(@PathVariable long locationId){
        Optional<Location> optionalLocation = locationService.findById(locationId);
        if(optionalLocation.isPresent()){
            return optionalLocation.get();
        }
        return null;
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
}
