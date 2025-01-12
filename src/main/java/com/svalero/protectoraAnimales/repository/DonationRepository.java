package com.svalero.protectoraAnimales.repository;

import com.svalero.protectoraAnimales.domain.Donation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends CrudRepository<Donation, Long> {
    List<Donation> findAll();
}
