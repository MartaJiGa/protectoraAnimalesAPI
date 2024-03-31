package com.svalero.protectoraAnimales.repository;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends CrudRepository<Location, Long> {
    List<Location> findAll();

    // Para "/locations":
    List<Location> findByCity(String city);
    List<Location> findByZipCode(int zipCode);
    List<Location> findByCityAndZipCode(String city, int zipCode);

    // Para "/location/{locationId}/animals":
    List<Animal> findByLocation(long locationId);
    List<Animal> findByLocationAndSpecies(long locationId, String species);
    List<Animal> findByLocationAndAge(long locationId, int age);
    List<Animal> findByLocationAndSize(long locationId, String size);
    List<Animal> findByLocationAndSpeciesAndAge(long locationId, String species, int age);
    List<Animal> findByLocationAndSpeciesAndSize(long locationId, String species, String size);
    List<Animal> findByLocationAndAgeAndSize(long locationId, int age, String size);
    List<Animal> findByLocationAndSpeciesAndAgeAndSize(long locationId, String species, int age, String size);
}
