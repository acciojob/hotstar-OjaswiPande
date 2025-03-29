package com.driver.services;

import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductionHouseService {

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;

    public Integer addProductionHouseToDb(ProductionHouseEntryDto productionHouseEntryDto){
        // Create a new ProductionHouse from DTO
        ProductionHouse productionHouse = new ProductionHouse();
        productionHouse.setName(productionHouseEntryDto.getName());
        productionHouse.setRatings(0); // Initialize with 0 rating

        // Save to database
        ProductionHouse savedProductionHouse = productionHouseRepository.save(productionHouse);
        return savedProductionHouse.getId();
    }

    public ProductionHouse getProductionHouseWithHighestRatings(){
        List<ProductionHouse> productionHouses = productionHouseRepository.findAll();

        if(productionHouses.isEmpty()) {
            return null;
        }

        ProductionHouse highestRated = productionHouses.get(0);
        for(ProductionHouse ph : productionHouses) {
            if(ph.getRatings() > highestRated.getRatings()) {
                highestRated = ph;
            }
        }
        return highestRated;
    }

    @Transactional
    public void updateRatingsForProductionHouse(int productionHouseId) {
        ProductionHouse productionHouse = productionHouseRepository.findById(productionHouseId).orElse(null);
        if(productionHouse == null) {
            return;
        }

        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        if(webSeriesList.isEmpty()) {
            productionHouse.setRatings(0);
            productionHouseRepository.save(productionHouse);
            return;
        }

        double sum = 0;
        for(WebSeries webSeries : webSeriesList) {
            sum += webSeries.getRating();
        }
        double averageRating = sum / webSeriesList.size();
        productionHouse.setRatings(averageRating);
        productionHouseRepository.save(productionHouse);
    }
}