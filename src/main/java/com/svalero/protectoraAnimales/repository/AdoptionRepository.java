package com.svalero.protectoraAnimales.repository;

import com.svalero.protectoraAnimales.domain.Adoption;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdoptionRepository extends CrudRepository<Long, Adoption> {
}
