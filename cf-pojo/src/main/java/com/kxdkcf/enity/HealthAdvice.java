package com.kxdkcf.enity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class HealthAdvice {
    @JsonProperty("dietaryAdvice")
    private List<String> dietaryAdvice;

    @JsonProperty("exerciseAndFitnessAdvice")
    private List<String> exerciseAndFitnessAdvice;

    @JsonProperty("adviceOnDailyRoutine")
    private List<String> adviceOnDailyRoutine;
}