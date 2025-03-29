package com.driver.services;

import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;

    public Integer addUser(User user) {
        // Save the user to the database
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId) {
        // Get the user
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return 0;
        }

        // Get user's subscription
        Subscription subscription = user.getSubscription();
        if (subscription == null) {
            return 0; // No subscription means no access
        }

        // Get all web series
        List<WebSeries> allWebSeries = webSeriesRepository.findAll();

        // Filter based on age limit and subscription type
        long count = allWebSeries.stream()
                .filter(webSeries -> webSeries.getAgeLimit() <= user.getAge()) // Age check
                .filter(webSeries -> isContentAccessible(subscription.getSubscriptionType(), webSeries.getSubscriptionType()))
                .count();

        return (int) count;
    }

    private boolean isContentAccessible(SubscriptionType userSubscription, SubscriptionType contentSubscription) {
        // ELITE can watch everything
        if (userSubscription == SubscriptionType.ELITE) {
            return true;
        }
        // PRO can watch PRO and BASIC
        else if (userSubscription == SubscriptionType.PRO) {
            return contentSubscription == SubscriptionType.PRO ||
                    contentSubscription == SubscriptionType.BASIC;
        }
        // BASIC can only watch BASIC
        else if (userSubscription == SubscriptionType.BASIC) {
            return contentSubscription == SubscriptionType.BASIC;
        }
        return false;
    }
}