package com.example.RankingApplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class RankingDecayScheduler {

    private final PlayerService playerService;

    // Todos los lunes a las 06:00 AM
    @Scheduled(cron = "0 0 6 ? * MON", zone = "Atlantic/Canary")
    public void applyWeeklyRankingDecay() {
        log.info("🏁 Iniciando decay semanal de rankings...");
        playerService.applyDecayToAllPlayers();
        log.info("✅ Decay semanal aplicado correctamente.");
    }
}