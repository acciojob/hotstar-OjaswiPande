package com.driver.controllers;

import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.services.ProductionHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production")
public class ProductionHouseController {

    @Autowired
    private ProductionHouseService productionHouseService;

    // Add a new production house
    @PostMapping("/add")
    public ResponseEntity<Integer> addProductionHouse(@RequestBody ProductionHouseEntryDto productionHouseEntryDto) {
        System.out.println("Received: " + productionHouseEntryDto); // Debug log
        try {
            if(productionHouseEntryDto.getName() == null || productionHouseEntryDto.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }

            Integer productionHouseId = productionHouseService.addProductionHouseToDb(productionHouseEntryDto);
            return new ResponseEntity<>(productionHouseId, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Print full error
            return new ResponseEntity<>(-1, HttpStatus.BAD_REQUEST);
        }
    }

    // Get the highest rated production house
    @GetMapping("/highest-rated")
    public ResponseEntity<ProductionHouse> getHighestRatedProductionHouse() {
        try {
            ProductionHouse productionHouse = productionHouseService.getProductionHouseWithHighestRatings();
            if (productionHouse != null) {
                return new ResponseEntity<>(productionHouse, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update production house ratings (if you add this to service later)
    @PutMapping("/update-ratings/{productionHouseId}")
    public ResponseEntity<String> updateProductionHouseRatings(@PathVariable int productionHouseId) {
        try {
            productionHouseService.updateRatingsForProductionHouse(productionHouseId);
            return new ResponseEntity<>("Ratings updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}