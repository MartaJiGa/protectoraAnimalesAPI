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
    List<Location> findByZipCode(String zipCode);
    List<Location> findByCityAndZipCode(String city, String zipCode);

    // Para "/location/{locationId}/animals":
    List<Animal> findByLocationId(long locationId);
}