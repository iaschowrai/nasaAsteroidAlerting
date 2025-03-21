package com.asteroid.controller;

import com.asteroid.service.AsteroidAlertingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/asteroid-alerting")
@RequiredArgsConstructor
public class AsteroidAlertingController {

    @Autowired
    private final AsteroidAlertingService asteroidAlertingService;

    @PostMapping("/alert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void alert(){
        asteroidAlertingService.alert();
    }

}
