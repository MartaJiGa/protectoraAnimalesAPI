package com.svalero.protectoraAnimales.repository;

import com.svalero.protectoraAnimales.domain.Donation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends CrudRepository<Long, Donation> {
}
