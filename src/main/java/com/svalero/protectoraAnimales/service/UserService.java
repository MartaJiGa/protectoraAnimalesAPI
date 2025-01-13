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
    public List<User> getUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron usuarios.");
        }
        return users;
    }
    public List<User> findByName(String name) {
        List<User> users = userRepository.findByName(name);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron usuarios con el nombre " + name);
        }
        return users;
    }
    public List<User> findBySurname(String surname) {
        List<User> users = userRepository.findBySurname(surname);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron usuarios de apellido " + surname);
        }
        return users;
    }
    public List<User> findByNameAndSurname(String name, String surname) {
        List<User> users = userRepository.findByNameAndSurname(name, surname);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron usuarios con el nombre " + name + " y apellido " + surname);
        }
        return users;
    }
    // endregion

    // region POST request
    public User saveUser(User user){
        return userRepository.save(user);
    }
    // endregion

    // region DELETE request
    public void removeUser(long userId){
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario con id " + userId + " no encontrado.");
        }
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
