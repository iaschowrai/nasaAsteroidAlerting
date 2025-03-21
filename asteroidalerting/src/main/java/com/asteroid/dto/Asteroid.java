package com.asteroid.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Asteroid {
        private String id;
        private String name;

        @JsonProperty("estimated_diameter")
        private EstimateDiameter estimateDiameter;

        @JsonProperty("is_potentially_hazardous_asteroid")  // Add this annotation for correct mapping
        private boolean potentiallyHazardous;

        @JsonProperty("close_approach_data")
        private List<CloseApproachData> closeApproachData;

}
