package com.svalero.protectoraAnimales.repository;

import com.svalero.protectoraAnimales.domain.Animal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends CrudRepository<Animal, Long> {
    List<Animal> findAll();
    List<Animal> findBySpecies(String species);
    List<Animal> findByAge(int age);
    List<Animal> findBySize(String size);
    List<Animal> findBySpeciesAndAge(String species, int age);
    List<Animal> findBySpeciesAndSize(String species, String size);
    List<Animal> findByAgeAndSize(int age, String species);
    List<Animal> findBySpeciesAndAgeAndSize(String species, int age, String size);
    List<Animal> findByLocationId(long locationId);

    @Query("SELECT a FROM Animal a WHERE a.adopted = false AND a.location.id = :locationId")
    List<Animal> findUnadoptedAnimalsByLocation(@Param("locationId") long locationId);
}
