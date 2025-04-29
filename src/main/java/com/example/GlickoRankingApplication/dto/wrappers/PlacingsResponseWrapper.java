package com.example.GlickoRankingApplication.dto.wrappers;

import com.example.GlickoRankingApplication.dto.bcp.PlayerPlayer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacingsResponseWrapper {

    private List<PlayerPlayer> active;  // Lista de jugadores (PlayerJson)

}
