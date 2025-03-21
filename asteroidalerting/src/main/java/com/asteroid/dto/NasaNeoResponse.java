package com.asteroid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NasaNeoResponse {

    @JsonProperty("element_count")
    private Long totalAsteroid;

    @JsonProperty("near_earth_objects")
    private Map<String , List<Asteroid>> nearEarthObjects;


}
