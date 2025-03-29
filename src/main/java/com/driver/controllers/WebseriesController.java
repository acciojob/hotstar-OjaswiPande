package com.driver.controllers;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.services.WebSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web-series")
public class WebseriesController {

    @Autowired
    private WebSeriesService webSeriesService;

    @PostMapping("/add")
    public ResponseEntity<Integer> addWebSeries(@RequestBody WebSeriesEntryDto webSeriesEntryDto) {
        try {
            System.out.println("Received DTO: " + webSeriesEntryDto); // Debug log
            Integer webSeriesId = webSeriesService.addWebSeries(webSeriesEntryDto);
            return new ResponseEntity<>(webSeriesId, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace
            if (e.getMessage().contains("Series is already present")) {
                return new ResponseEntity<>(-1, HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(-1, HttpStatus.BAD_REQUEST);
        }
    }
}