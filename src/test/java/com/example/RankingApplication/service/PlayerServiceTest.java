package com.example.RankingApplication.service;

import com.example.RankingApplication.client.BCPClient;
import com.example.RankingApplication.dto.PlayerDTO;
import com.example.RankingApplication.dto.bcp.PlayerPlayer;
import com.example.RankingApplication.enums.Faction;
import com.example.RankingApplication.model.FactionPlayed;
import com.example.RankingApplication.model.Player;
import com.example.RankingApplication.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PlayerServiceTest {

    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private BCPClient bcpClient;

    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock players
        FactionPlayed aeldari = new FactionPlayed("1",Faction.AELDARI,3);
        HashMap<Faction, FactionPlayed> map = new HashMap<>();
        map.put(Faction.AELDARI, aeldari);


        player1 = new Player("Player 1");
        player1.setId("1");
        player1.setRating(1500);
        player1.setLastMatchDate(LocalDateTime.now().minusDays(10));

        player1.setFactionsPlayed(map);


        player2 = new Player("Player 2");
        player2.setId("2");
        player2.setRating(1450);
        player2.setLastMatchDate(LocalDateTime.now().minusDays(35));
        player2.setFactionsPlayed(map);
    }

    @Test
    void testGetAllPlayers() {
        // Arrange
        when(playerRepository.findAll()).thenReturn(Arrays.asList(player1, player2));

        // Act
        List<PlayerDTO> playerDTOs = playerService.getAllPlayers();

        // Assert
        assertEquals(2, playerDTOs.size());
        assertEquals("Player 1", playerDTOs.get(0).name());
        assertEquals(1500, playerDTOs.get(0).rating());
    }

    @Test
    void testApplyDecayToAllPlayers() {
        // Arrange
        when(playerRepository.findAll()).thenReturn(Arrays.asList(player1, player2));

        // Act
        playerService.applyDecayToAllPlayers();

        // Assert
        verify(playerRepository, times(1)).saveAll(anyList());
        assertTrue(player1.getRating() > 0);
        assertTrue(player2.getRating() < 1450);  // Player2 should have decayed
    }

    @Test
    void testCreatePlayersFromBCP() {
        // Arrange
        PlayerPlayer mockPlayer = new PlayerPlayer();
        mockPlayer.setUser(new PlayerPlayer.User("3","John","Doe"));

        when(bcpClient.getPlayers("eventId")).thenReturn(List.of(mockPlayer));
        when(playerRepository.existsById("3")).thenReturn(false);

        // Act
        playerService.createPlayersFromBCP("eventId");

        // Assert
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void testRemoveAllPlayers() {
        // Act
        playerService.removeAllPlayers();

        // Assert
        verify(playerRepository, times(1)).deleteAll();
    }

    @Test
    void testRemovePlayerById() {
        // Act
        playerService.removePlayerById("1");

        // Assert
        verify(playerRepository, times(1)).deleteById("1");
    }

    @Test
    void testGetMostPlayedFaction() {
        // Arrange
        FactionPlayed factionPlayed = new FactionPlayed(player1.getId(), Faction.ASTRA_MILITARUM, 5);
        player1.setFactionsPlayed(new HashMap<>());
        player1.getFactionsPlayed().put(Faction.ASTRA_MILITARUM, factionPlayed);

        // Act
        Faction mostPlayedFaction = playerService.getMostPlayedFaction(player1.getFactionsPlayed());

        // Assert
        assertEquals(Faction.ASTRA_MILITARUM, mostPlayedFaction);
    }
}
