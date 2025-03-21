package com.asteroid.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiameterRange {

    @JsonProperty("estimate_diameter_min")
    private double minDiameter;
    @JsonProperty("estimate_diameter_max")
    private double maxDiameter;
}
