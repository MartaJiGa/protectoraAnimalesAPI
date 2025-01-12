package com.svalero.protectoraAnimales.repository;

import com.svalero.protectoraAnimales.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll();
    List<User> findByName(String name);
    List<User> findBySurname(String surname);
    List<User> findByNameAndSurname(String name, String surname);
}
