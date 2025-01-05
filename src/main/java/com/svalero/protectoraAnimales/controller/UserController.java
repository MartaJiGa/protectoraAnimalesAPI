package com.svalero.protectoraAnimales.controller;

import com.svalero.protectoraAnimales.domain.ErrorResponse;
import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.exception.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // region GET requests
    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable long userId) throws ResourceNotFoundException {
        Optional<User> optionalUser = userService.findById(userId);
        return optionalUser.orElseThrow(()->new ResourceNotFoundException(userId));
    }
    @GetMapping("/users")
    public List<User> findAll(@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String surname){
        if(!name.isEmpty() && surname.isEmpty()){
            return userService.findByName(name);
        }
        else if(name.isEmpty() && !surname.isEmpty()){
            return userService.findBySurname(surname);
        }
        else if(!name.isEmpty() && !surname.isEmpty()){
            return userService.findByNameAndSurname(name, surname);
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

    //region EXCEPTION HANDLER
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException resNotFoundEx){
        ErrorResponse errorResponse = new ErrorResponse(404, resNotFoundEx.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    // endregion
}
