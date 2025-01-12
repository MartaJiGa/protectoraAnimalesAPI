package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // region GET requests
    public User findById(long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public List<User> findByName(String name){
        return userRepository.findByName(name);
    }

    public List<User> findBySurname(String surname){
        return userRepository.findBySurname(surname);
    }

    public List<User> findByNameAndSurname(String name, String surname){
        return userRepository.findByNameAndSurname(name, surname);
    }
    // endregion

    // region POST request
    public User saveUser(User user){
        return userRepository.save(user);
    }
    // endregion

    // region DELETE request
    public void removeUser(long userId){
        userRepository.deleteById(userId);
    }
    // endregion

    // region PUT request
    public User modifyUser(User newUser, long userId){
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        existingUser.setUsername(newUser.getUsername());
        existingUser.setName(newUser.getName());
        existingUser.setSurname(newUser.getSurname());
        existingUser.setDateOfBirth(newUser.getDateOfBirth());
        existingUser.setEmail(newUser.getEmail());

        return userRepository.save(existingUser);
    }
    // endregion
}
