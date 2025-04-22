package com.example.GlickoRankingApplication.dto;

import java.util.List;

public record CreatePlayersRequest(List<CreatePlayerRequest> players) { }