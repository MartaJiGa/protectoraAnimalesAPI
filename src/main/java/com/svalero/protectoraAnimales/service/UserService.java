package com.svalero.protectoraAnimales.service;

import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // region GET requests
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public List<User> getUserByName(String name){
        return userRepository.findByName(name);
    }

    public List<User> getUserBySurname(String surname){
        return userRepository.findBySurname(surname);
    }

    public List<User> getUserByNameAndSurname(String name, String surname){
        return userRepository.findByNameAndSurname(name, surname);
    }

    public Optional<User> getUserById(long userId){
        return userRepository.findById(userId);
    }
    // endregion

    // region POST request
    public void saveUser(User user){
        userRepository.save(user);
    }
    // endregion

    // region DELETE request
    public void removeUser(long userId){
        userRepository.deleteById(userId);
    }
    // endregion

    // region PUT request
    public void modifyUser(User newUser, long userId){
        Optional<User> user = userRepository.findById(userId);

        if(user.isPresent()){
            User existingUser = user.get();

            existingUser.setUsername(newUser.getUsername());
            existingUser.setName(newUser.getName());
            existingUser.setSurname(newUser.getSurname());
            existingUser.setDateOfBirth(newUser.getDateOfBirth());
            existingUser.setEmail(newUser.getEmail());

            userRepository.save(existingUser);
        }
    }
    // endregion
}
