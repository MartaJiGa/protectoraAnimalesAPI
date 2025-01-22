package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.domain.dto.user.UserChangeEmailInDTO;
import com.svalero.protectoraAnimales.domain.dto.user.UserInDTO;
import com.svalero.protectoraAnimales.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    // region GET requests
    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable long userId) {
        logger.info("BEGIN getUser()");
        User user = userService.findById(userId);
        logger.info("END getUser()");
        return user;
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> findAllUsers(@RequestParam(defaultValue = "") String name,
                                                   @RequestParam(defaultValue = "") String surname,
                                                   @RequestParam(required = false) Boolean includeAdoptionsAndDonations){
        List<User> users;

        if (includeAdoptionsAndDonations == null) {
            includeAdoptionsAndDonations = false;
        }

        if(!name.isEmpty() && surname.isEmpty()){
            logger.info("BEGIN findAllUsers() -> ByName");
            users = userService.findByName(name);
            logger.info("END findAllUsers() -> ByName");
        }
        else if(name.isEmpty() && !surname.isEmpty()){
            logger.info("BEGIN findAllUsers() -> BySurname");
            users = userService.findBySurname(surname);
            logger.info("END findAllUsers() -> BySurname");
        }
        else if(!name.isEmpty() && !surname.isEmpty()){
            logger.info("BEGIN findAllUsers() -> ByNameAndSurname");
            users = userService.findByNameAndSurname(name, surname);
            logger.info("END findAllUsers() -> ByNameAndSurname");
        }
        else if (includeAdoptionsAndDonations) {
            logger.info("BEGIN findAllUsers() -> WithAdoptionsAndDonations");
            users = userService.findUsersWithAdoptionsAndDonations();
            logger.info("END findAllUsers() -> WithAdoptionsAndDonations");
        }
        else{
            logger.info("BEGIN findAllUsers()");
            users = userService.getUsers();
            logger.info("END findAllUsers()");
        }

        return ResponseEntity.ok(users);
    }
    // endregion

    // region POST request
    @PostMapping("/users")
    public ResponseEntity<User> saveUser(@Valid @RequestBody UserInDTO user){
        logger.info("BEGIN saveUser()");
        User savedUser = userService.saveUser(user);
        logger.info("END saveUser()");
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    // endregion

    // region DELETE request
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable long userId){
        logger.info("BEGIN removeUser()");
        userService.removeUser(userId);
        logger.info("END removeUser()");
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region PUT request
    @PutMapping("/user/{userId}")
    public ResponseEntity<User> modifyUser(@Valid @RequestBody UserInDTO user,
                                           @PathVariable long userId){
        logger.info("BEGIN modifyUser()");
        User savedUser = userService.modifyUser(user, userId);
        logger.info("END modifyUser()");
        return ResponseEntity.ok(savedUser);
    }
    // endregion

    // region PATCH request
    @PatchMapping("/user/{userId}/email")
    public ResponseEntity<User> changeUserEmail(@PathVariable long userId,
                                                @Valid @RequestBody UserChangeEmailInDTO userChangeEmail){
        logger.info("BEGIN changeUserEmail()");
        User changedEmail = userService.changeUserEmail(userId, userChangeEmail);
        logger.info("END changeUserEmail()");
        return ResponseEntity.ok(changedEmail);
    }
    // endregion
}
