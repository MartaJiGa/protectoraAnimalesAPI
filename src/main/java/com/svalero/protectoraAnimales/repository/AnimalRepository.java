package com.svalero.protectoraAnimales.repository;

import com.svalero.protectoraAnimales.domain.Animal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends CrudRepository<Long, Animal> {
}
