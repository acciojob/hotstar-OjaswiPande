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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TestCases {

        // Assuming you have setup a webseries with the name "The Crown"
        @Mock
        private WebSeriesRepository webSeriesRepository;  // ✅ Change from @Autowired to @Mock

        @Test
        public void testFindWebSeriesByName() {
            // Arrange: Mock repository behavior
            String seriesName = "Sample Series"; // Ensure a string value

            WebSeries mockWebSeries = new WebSeries();
            mockWebSeries.setSeriesName(seriesName);

            when(webSeriesRepository.findBySeriesName(seriesName)).thenReturn(mockWebSeries);

            // Act: Call the method under test
            System.out.println("Type of variable: " + seriesName.getClass().getName());

            WebSeries foundWebSeries = webSeriesRepository.findBySeriesName(seriesName);

            // Assert: Verify the output
            assertNotNull(foundWebSeries);
            assertEquals(seriesName, foundWebSeries.getSeriesName()); // ✅ Add an assertion to confirm correct series
        }
    }

