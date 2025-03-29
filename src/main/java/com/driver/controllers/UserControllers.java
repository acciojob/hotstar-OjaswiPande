package com.driver.controllers;

import com.driver.model.User;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserControllers {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<Integer> addUser(@RequestBody User user) {
        try {
            Integer userId = userService.addUser(user);
            return new ResponseEntity<>(userId, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(-1, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/available-web-series")
    public ResponseEntity<Integer> getAvailableCountOfWebSeriesViewable(
            @RequestParam("userId") Integer userId) {
        try {
            Integer count = userService.getAvailableCountOfWebSeriesViewable(userId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(-1, HttpStatus.BAD_REQUEST);
        }
    }
}