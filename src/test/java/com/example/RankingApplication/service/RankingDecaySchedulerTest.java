package com.example.RankingApplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@EnableScheduling
class RankingDecaySchedulerTest {

    @InjectMocks
    private RankingDecayScheduler rankingDecayScheduler;

    @Mock
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApplyWeeklyRankingDecay() {
        // Act
        rankingDecayScheduler.applyWeeklyRankingDecay();

        // Assert
        verify(playerService, times(1)).applyDecayToAllPlayers(); // Verifica que applyDecayToAllPlayers es llamado una vez
    }
}
