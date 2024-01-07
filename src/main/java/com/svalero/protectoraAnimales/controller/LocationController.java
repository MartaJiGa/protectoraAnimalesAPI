package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/locations")
    public List<Location> findAll(){
        return locationService.getLocations();
    }

    @PostMapping("/locations")
    public void saveLocation(@RequestBody Location location){
        locationService.saveLocation(location);
    }
}
