package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // region GET requests
    @GetMapping("/users")
    public List<User> findAll(@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String surname){
        if(!name.isEmpty() && surname.isEmpty()){
            userService.getUserByName(name);
        }
        else if(name.isEmpty() && !surname.isEmpty()){
            userService.getUserBySurname(surname);
        }
        else if(!name.isEmpty() && !surname.isEmpty()){
            userService.getUserByNameAndSurname(name, surname);
        }
        return userService.getUsers();
    }
    // endregion

    // region POST request
    @PostMapping("/users")
    public void saveUser(@RequestBody User user){
        userService.saveUser(user);
    }
    // endregion

    // region DELETE request
    @DeleteMapping("/user/{userId}")
    public void removeUser(@PathVariable long userId){
        userService.removeUser(userId);
    }
    // endregion

    // region PUT request
    @PutMapping("/user/{userId}")
    public void modifyUser(@RequestBody User user, @PathVariable long userId){
        userService.modifyUser(user, userId);
    }
    // endregion
}
