package com.svalero.protectoraAnimales.repository;

import com.svalero.protectoraAnimales.domain.Adoption;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdoptionRepository extends CrudRepository<Adoption, Long> {
    List<Adoption> findAll();
    List<Adoption> findByAdoptionDate(LocalDate adoptionDate);
    List<Adoption> findByUserId(long userId);
    List<Adoption> findByAnimalId(long animalId);
    List<Adoption> findByAdoptionDateAndUserId(LocalDate adoptionDate, long userId);
    List<Adoption> findByAdoptionDateAndAnimalId(LocalDate adoptionDate, long animalId);
    List<Adoption> findByAnimalIdAndUserId(long animalId, long userId);
    List<Adoption> findByAdoptionDateAndAnimalIdAndUserId(LocalDate adoptionDate, long animalId, long userId);

    @Query("SELECT a FROM adoptions a WHERE a.pickUpDate BETWEEN :startDate AND :endDate")
    public List<Adoption> findPickUpsInNextTwoWeeks(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
