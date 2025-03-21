package com.asteroid.service;

import com.asteroid.client.NasaClient;
import com.asteroid.dto.Asteroid;
import com.asteroid.event.AsteroidCollisionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsteroidAlertingService {

    private final NasaClient nasaClient;

    private final KafkaTemplate<String, AsteroidCollisionEvent> kafkaTemplate;
    public void alert() {

        log.info("Alerting service called");

        // From and to date
        final LocalDate fromDate = LocalDate.now();
        final LocalDate toDate = LocalDate.now().plusDays(7);

        // call nasa api to get the asteroid data
        log.info("Getting asteroid list for dates: {} to {}", fromDate,toDate);
        final List<Asteroid> asteroidList= nasaClient.getNeoAsteroids(fromDate, toDate);

        log.info("Retrieved Asteroid list of size: {}", asteroidList.size());


        // if there are any hazardous asteroids, send an alert
        final List<Asteroid>  dangerousAsteroids = asteroidList
                .stream().filter(Asteroid::isPotentiallyHazardous)
                .toList();

        log.info("Found {} hazardous asteroids", dangerousAsteroids.size());

        // create an alert and put on kafka topic

        final List<AsteroidCollisionEvent> asteroidCollisonEventList =
                createEventListOfDangerousAsteroids(dangerousAsteroids);

        log.info("Sending asteroid list to Kafka : {}" , asteroidCollisonEventList.size());

        asteroidCollisonEventList.forEach(event ->{
            kafkaTemplate.send("asteroid-alert", event);
            log.info("Asteroid alert sent to kafka topic : {}", event);
        });
    }
//    private List<AsteroidCollisionEvent> createEventListOfDangerousAsteroids(List<Asteroid> dangerousAsteroids) {
//        return dangerousAsteroids.stream()
//                .map(asteroid -> {
//                    if (asteroid.isPotentiallyHazardous()) {
//                        // Use stream().findFirst() to safely get the first CloseApproachData
//                        return asteroid.getCloseApproachData().stream()
//                                .findFirst()
//                                .map(closeApproachData -> AsteroidCollisionEvent.builder()
//                                        .asteroidName(asteroid.getName())
//                                        .closeApproachDate(closeApproachData.getCloseApproachDate().toString())
//                                        .missDistanceKilometers(closeApproachData.getMissDistance().getKilometers())
//                                        .estimateDiameterAvgMeters((asteroid.getEstimateDiameter().getMeters().getMinDiameter() +
//                                                asteroid.getEstimateDiameter().getMeters().getMaxDiameter()) / 2)
//                                        .build())
//                                .orElse(null); // If no CloseApproachData is available, return null
//                    }
//                    return null; // If the asteroid is not hazardous, return null
//                })
//                .filter(Objects::nonNull) // Filter out null values (non-hazardous asteroids)
//                .toList();
//    }

    private List<AsteroidCollisionEvent> createEventListOfDangerousAsteroids(List<Asteroid> dangerousAsteroids) {
        return dangerousAsteroids.stream()
                .map(asteroid -> {
                    if (asteroid.isPotentiallyHazardous()){
                        return AsteroidCollisionEvent.builder()
                                .asteroidName(asteroid.getName())
                                .closeApproachDate(asteroid.getCloseApproachData().get(0).getCloseApproachDate().toString())
                                .missDistanceKilometers(asteroid.getCloseApproachData().get(0).getMissDistance().getKilometers())
                                .estimateDiameterAvgMeters((asteroid.getEstimateDiameter().getMeters().getMinDiameter() +
                                        asteroid.getEstimateDiameter().getMeters().getMaxDiameter()) / 2)
                                .build();
                    }
                    return null;
                }).toList();
    }

}
