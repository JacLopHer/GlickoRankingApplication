package com.example.RankingApplication.dto.wrappers;

import com.example.RankingApplication.dto.bcp.PlayerPlayer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacingsResponseWrapper {

    private List<PlayerPlayer> active;  // Lista de jugadores (PlayerJson)

}
