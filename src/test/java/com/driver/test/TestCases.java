package com.driver.test;

import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import com.driver.services.ProductionHouseService;
import com.driver.services.SubscriptionService;
import com.driver.services.UserService;
import com.driver.services.WebSeriesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TestCases {
    @Mock
    private WebSeriesRepository webSeriesRepository;

    @Mock
    private ProductionHouseRepository productionHouseRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WebSeriesService webSeriesService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private ProductionHouseService productionHouseService;

    private User user;
    private Subscription subscription;
    private WebSeries webSeries;
    private ProductionHouse productionHouse;
    private ProductionHouseEntryDto phEntryDto;
    private WebSeriesEntryDto wsEntryDto;
    private SubscriptionEntryDto subEntryDto;

    @BeforeEach
    public void setup() {
        // Initialize entities
        user = new User();
        user.setId(1);
        user.setAge(25);

        subscription = new Subscription();
        subscription.setId(1);
        subscription.setSubscriptionType(SubscriptionType.BASIC);
        subscription.setNoOfScreensSubscribed(0); // Changed to 0 screens
        subscription.setTotalAmountPaid(500); // 500 + 200*0
        user.setSubscription(subscription);

        productionHouse = new ProductionHouse();
        productionHouse.setId(1);
        productionHouse.setName("Test Productions");
        productionHouse.setRatings(4.5);
        productionHouse.setWebSeriesList(new ArrayList<>()); // Initialize empty list

        webSeries = new WebSeries();
        webSeries.setId(1);
        webSeries.setSeriesName("Test Series");
        webSeries.setAgeLimit(18);
        webSeries.setRating(4.5);
        webSeries.setSubscriptionType(SubscriptionType.BASIC);
        webSeries.setProductionHouse(productionHouse);

        // Initialize DTOs with proper constructors
        phEntryDto = new ProductionHouseEntryDto("Test Productions");

        wsEntryDto = new WebSeriesEntryDto(
                "Test Series",
                18,
                4.5,
                SubscriptionType.BASIC,
                1
        );

        subEntryDto = new SubscriptionEntryDto(1, SubscriptionType.BASIC, 0); // 0 screens
    }

    // ProductionHouseService Tests
    @Test
    public void testAddProductionHouseToDb() {
        when(productionHouseRepository.save(any(ProductionHouse.class)))
                .thenAnswer(invocation -> {
                    ProductionHouse ph = invocation.getArgument(0);
                    ph.setId(1);
                    return ph;
                });

        Integer result = productionHouseService.addProductionHouseToDb(phEntryDto);
        assertNotNull(result);
        assertEquals(1, result);
    }

    @Test
    public void testGetProductionHouseWithHighestRatings() {
        ProductionHouse ph2 = new ProductionHouse();
        ph2.setRatings(3.0);

        when(productionHouseRepository.findAll()).thenReturn(Arrays.asList(productionHouse, ph2));

        ProductionHouse result = productionHouseService.getProductionHouseWithHighestRatings();
        assertNotNull(result);
        assertEquals(4.5, result.getRatings());
    }

    // WebSeriesService Tests
    @Test
    public void testAddWebSeries_Success() throws Exception {
        when(webSeriesRepository.findAll()).thenReturn(new ArrayList<>());
        when(productionHouseRepository.findById(1)).thenReturn(Optional.of(productionHouse));
        when(webSeriesRepository.save(any(WebSeries.class))).thenReturn(webSeries);

        Integer result = webSeriesService.addWebSeries(wsEntryDto);
        assertNotNull(result);
        assertEquals(1, result);
    }

    @Test
    public void testAddWebSeries_DuplicateName() {
        when(webSeriesRepository.findAll()).thenReturn(Arrays.asList(webSeries));

        assertThrows(Exception.class, () -> webSeriesService.addWebSeries(wsEntryDto));
    }

    // SubscriptionService Tests
    @Test
    public void testBuySubscription() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        Integer amount = subscriptionService.buySubscription(subEntryDto);
        assertNotNull(amount);
        assertEquals(500, amount); // 500 + 200*0
    }

    @Test
    public void testUpgradeSubscription_BasicToPro() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Integer priceDifference = subscriptionService.upgradeSubscription(1);
        assertNotNull(priceDifference);
        assertEquals(300, priceDifference); // 800 (PRO) - 500 (BASIC) = 300
    }

    @Test
    public void testUpgradeSubscription_AlreadyElite() {
        subscription.setSubscriptionType(SubscriptionType.ELITE);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThrows(Exception.class, () -> subscriptionService.upgradeSubscription(1));
    }

    @Test
    public void testCalculateTotalRevenue() {
        Subscription proSubscription = new Subscription();
        proSubscription.setTotalAmountPaid(800); // PRO with 0 screens

        when(subscriptionRepository.findAll()).thenReturn(Arrays.asList(subscription, proSubscription));

        Integer revenue = subscriptionService.calculateTotalRevenueOfHotstar();
        assertEquals(1300, revenue); // 500 (BASIC) + 800 (PRO)
    }

    // UserService Tests
    @Test
    public void testAddUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        Integer userId = userService.addUser(user);
        assertNotNull(userId);
        assertEquals(1, userId);
    }

    @Test
    public void testGetAvailableWebSeriesCount() {
        WebSeries proSeries = new WebSeries();
        proSeries.setSubscriptionType(SubscriptionType.PRO);
        proSeries.setAgeLimit(20);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(webSeriesRepository.findAll()).thenReturn(Arrays.asList(webSeries, proSeries));

        Integer count = userService.getAvailableCountOfWebSeriesViewable(1);
        assertNotNull(count);
        assertEquals(1, count); // Only BASIC series available to BASIC user
    }

    @Test
    public void testGetAvailableWebSeriesCount_ProUser() {
        // Upgrade user to PRO
        subscription.setSubscriptionType(SubscriptionType.PRO);

        WebSeries proSeries = new WebSeries();
        proSeries.setSubscriptionType(SubscriptionType.PRO);
        proSeries.setAgeLimit(20);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(webSeriesRepository.findAll()).thenReturn(Arrays.asList(webSeries, proSeries));

        Integer count = userService.getAvailableCountOfWebSeriesViewable(1);
        assertNotNull(count);
        assertEquals(2, count); // Both BASIC and PRO series available to PRO user
    }

}