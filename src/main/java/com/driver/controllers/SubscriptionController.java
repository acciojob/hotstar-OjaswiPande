package com.driver.controllers;

import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/buy")
    public ResponseEntity<Integer> buySubscription(@RequestBody SubscriptionEntryDto subscriptionEntryDto) {
        try {
            Integer totalAmount = subscriptionService.buySubscription(subscriptionEntryDto);
            return new ResponseEntity<>(totalAmount, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(-1, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/upgradeSubscription/{userId}")
    public ResponseEntity<Integer> upgradeSubscription(@PathVariable Integer userId) {
        try {
            Integer priceDifference = subscriptionService.upgradeSubscription(userId);
            return new ResponseEntity<>(priceDifference, HttpStatus.OK);
        } catch (Exception e) {
            if (e.getMessage().contains("Already the best Subscription")) {
                return new ResponseEntity<>(-1, HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(-1, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/calculateTotalRevenue")
    public ResponseEntity<Integer> getTotalRevenue() {
        try {
            Integer totalRevenue = subscriptionService.calculateTotalRevenueOfHotstar();
            return new ResponseEntity<>(totalRevenue, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(-1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}