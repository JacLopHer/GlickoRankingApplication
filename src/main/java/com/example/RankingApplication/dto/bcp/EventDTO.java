package com.example.RankingApplication.dto.bcp;

public record EventDTO(
        String name,
        StatusDTO status,
        GameSystemDTO gameSystem,
        String id
) {
    public record StatusDTO(Integer numberOfRounds){}
    public record GameSystemDTO(
            String name,
            Integer code
    ) {}
}