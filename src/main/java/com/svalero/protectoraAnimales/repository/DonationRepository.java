package com.svalero.protectoraAnimales.repository;

import com.svalero.protectoraAnimales.domain.Donation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonationRepository extends CrudRepository<Donation, Long> {
    List<Donation> findAll();
    List<Donation> findByDonationDate(LocalDate donationDate);
    List<Donation> findByUserId(long userId);
    List<Donation> findByDonationDateAndUserId(LocalDate donationDate, long userId);
}
