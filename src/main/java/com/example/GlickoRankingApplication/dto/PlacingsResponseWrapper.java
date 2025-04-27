package com.example.GlickoRankingApplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacingsResponseWrapper {

    private List<PlayerJson> active;  // Lista de jugadores (PlayerJson)

}
