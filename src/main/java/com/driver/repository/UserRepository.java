package com.driver.repository;

import com.driver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Custom query to find users by age
    List<User> findByAge(int age);

    // Custom query to find users with active subscriptions
    List<User> findBySubscriptionIsNotNull();
}