package com.driver.services;

import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto) {
        // Get the user
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).orElse(null);
        if (user == null) {
            return -1; // or throw exception
        }

        // Create new subscription
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());
        subscription.setTotalAmountPaid(calculateTotalAmount(
                subscriptionEntryDto.getSubscriptionType(),
                subscriptionEntryDto.getNoOfScreensRequired()
        ));
        subscription.setUser(user);

        // Save subscription
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Update user's subscription
        user.setSubscription(savedSubscription);
        userRepository.save(user);

        return savedSubscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId) throws Exception {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getSubscription() == null) {
            throw new Exception("User or subscription not found");
        }

        Subscription currentSubscription = user.getSubscription();

        // Check if already ELITE
        if (currentSubscription.getSubscriptionType() == SubscriptionType.ELITE) {
            throw new Exception("Already the best Subscription");
        }

        SubscriptionType newType;
        int priceDifference;

        if (currentSubscription.getSubscriptionType() == SubscriptionType.BASIC) {
            newType = SubscriptionType.PRO;
            priceDifference = (800 + 250 * currentSubscription.getNoOfScreensSubscribed()) -
                    currentSubscription.getTotalAmountPaid();
        } else { // PRO to ELITE
            newType = SubscriptionType.ELITE;
            priceDifference = (1000 + 350 * currentSubscription.getNoOfScreensSubscribed()) -
                    currentSubscription.getTotalAmountPaid();
        }

        // Update subscription
        currentSubscription.setSubscriptionType(newType);
        currentSubscription.setTotalAmountPaid(calculateTotalAmount(
                newType,
                currentSubscription.getNoOfScreensSubscribed()
        ));
        subscriptionRepository.save(currentSubscription);

        return priceDifference;
    }

    public Integer calculateTotalRevenueOfHotstar() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        return subscriptions.stream()
                .mapToInt(Subscription::getTotalAmountPaid)
                .sum();
    }

    private Integer calculateTotalAmount(SubscriptionType type, int noOfScreens) {
        switch (type) {
            case BASIC:
                return 500 + 200 * noOfScreens;
            case PRO:
                return 800 + 250 * noOfScreens;
            case ELITE:
                return 1000 + 350 * noOfScreens;
            default:
                return 0;
        }
    }
}