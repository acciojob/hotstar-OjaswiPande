package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto) throws Exception {
        // Check if web series already exists by checking all series names
        List<WebSeries> allWebSeries = webSeriesRepository.findAll();
        boolean seriesExists = allWebSeries.stream()
                .anyMatch(ws -> ws.getSeriesName().equals(webSeriesEntryDto.getSeriesName()));

        if (seriesExists) {
            throw new Exception("Series is already present");
        }

        // Get production house
        ProductionHouse productionHouse = productionHouseRepository
                .findById(webSeriesEntryDto.getProductionHouseId())
                .orElseThrow(() -> new Exception("Production House not found"));

        // Create new web series
        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setProductionHouse(productionHouse);

        // Save web series
        WebSeries savedWebSeries = webSeriesRepository.save(webSeries);

        // Update production house's web series list
        productionHouse.getWebSeriesList().add(savedWebSeries);

        // Recalculate and update production house ratings
        updateProductionHouseRatings(productionHouse);

        return savedWebSeries.getId();
    }

    private void updateProductionHouseRatings(ProductionHouse productionHouse) {
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        if (webSeriesList.isEmpty()) {
            productionHouse.setRatings(0);
        } else {
            double average = webSeriesList.stream()
                    .mapToDouble(WebSeries::getRating)
                    .average()
                    .orElse(0.0);
            productionHouse.setRatings(average);
        }
        productionHouseRepository.save(productionHouse);
    }
}