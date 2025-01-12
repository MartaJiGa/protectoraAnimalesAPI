package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // region GET requests
    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable long userId) {
        return userService.findById(userId);
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> findAll(@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String surname){
        List<User> users;

        if(!name.isEmpty() && surname.isEmpty()){
            users = userService.findByName(name);
        }
        else if(name.isEmpty() && !surname.isEmpty()){
            users = userService.findBySurname(surname);
        }
        else if(!name.isEmpty() && !surname.isEmpty()){
            users = userService.findByNameAndSurname(name, surname);
        }
        else{
            users = userService.getUsers();
        }

        return ResponseEntity.ok(users);
    }
    // endregion

    // region POST request
    @PostMapping("/users")
    public ResponseEntity<User> saveUser(@Valid @RequestBody User user){
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    // endregion

    // region DELETE request
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable long userId){
        userService.removeUser(userId);
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region PUT request
    @PutMapping("/user/{userId}")
    public ResponseEntity<User> modifyUser(@Valid @RequestBody User user, @PathVariable long userId){
        userService.modifyUser(user, userId);
        return ResponseEntity.ok(user);
    }
    // endregion
}
