package com.driver.repository;

import com.driver.model.SubscriptionType;
import com.driver.model.WebSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WebSeriesRepository extends JpaRepository<WebSeries, Integer> {
    // Custom query to find web series by subscription type
    List<WebSeries> findBySubscriptionType(SubscriptionType type);

    // Custom query to find web series by production house
    List<WebSeries> findByProductionHouseId(int productionHouseId);

    // Custom query to find web series by minimum rating
    List<WebSeries> findByRatingGreaterThanEqual(double minRating);
}