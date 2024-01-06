package com.svalero.protectoraAnimales.repository;

import com.svalero.protectoraAnimales.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<Long, User> {
}
