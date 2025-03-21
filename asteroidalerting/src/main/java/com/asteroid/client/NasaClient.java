package com.asteroid.client;

import com.asteroid.dto.Asteroid;
import com.asteroid.dto.NasaNeoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class NasaClient {

    @Value("${nasa.neo.api.url}")
    private String nasaNeoApiUrl;

    @Value("${nasa.api.key}")
    private String nasaApiKey;

    private final RestTemplate restTemplate;

    public NasaClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Asteroid> getNeoAsteroids(final LocalDate fromDate, final LocalDate toDate) {

        try {
            NasaNeoResponse nasaNeoResponse =
                    restTemplate.getForObject(getUrl(fromDate, toDate), NasaNeoResponse.class);

//            return Optional.ofNullable(nasaNeoResponse)
//                    .map(NasaNeoResponse::getNearEarthObjects)
//                    .map(map ->map.values().stream().flatMap(List::stream).toList())
//                    .orElseGet(ArrayList::new);

            List<Asteroid> asteroidList = new ArrayList<>();
            if (nasaNeoResponse != null) {
                asteroidList.addAll(nasaNeoResponse.getNearEarthObjects()
                        .values()
                        .stream()
                        .flatMap(List::stream)
                        .toList());
            }
//            System.out.println("Asteroid id list: " + asteroidList.stream().filter( asteroid -> asteroid.getId().equals("54178992")).toList());

            return asteroidList;
        } catch(RestClientException ex) {
            System.err.println("Error fetching data from NASA API: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    private String getUrl(final LocalDate fromDate, final LocalDate toDate) {
        String apiUrl =
                UriComponentsBuilder.fromUri(URI.create(nasaNeoApiUrl))
                        .queryParam("start_date", fromDate)
                        .queryParam("end_date", toDate)
                        .queryParam("api_key",nasaApiKey)
                        .toUriString();
        return apiUrl;
    }
}


