package com.driver.repository;

import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    // Custom query to find subscriptions by type
    List<Subscription> findBySubscriptionType(SubscriptionType type);

    // Custom query to find subscription by user ID
    Subscription findByUserId(int userId);
}