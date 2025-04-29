package com.example.RankingApplication.service;

import com.example.RankingApplication.client.BCPClient;
import com.example.RankingApplication.dto.MatchDTO;
import com.example.RankingApplication.dto.bcp.PlayerPairing;
import com.example.RankingApplication.enums.Faction;
import com.example.RankingApplication.exceptions.PlayerNotFoundException;
import com.example.RankingApplication.model.Match;
import com.example.RankingApplication.model.Player;
import com.example.RankingApplication.repository.FactionPlayedRepository;
import com.example.RankingApplication.repository.MatchRepository;
import com.example.RankingApplication.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MatchServiceTest {

    @InjectMocks
    private MatchService matchService;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private GlickoRatingService glickoRatingService;

    @Mock
    private FactionPlayedRepository factionPlayedRepository;

    @Mock
    private PlayerService playerService;

    @Mock
    private BCPClient bcpClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteAllMatches_shouldDeleteAllMatches() {
        matchService.deleteAllMatches();
        verify(matchRepository, times(1)).deleteAll();
    }

    @Test
    void getAllMatches_shouldReturnAllMatches() {
        List<Match> mockMatches = List.of(new Match(), new Match());
        when(matchRepository.findAll()).thenReturn(mockMatches);

        List<Match> result = matchService.getAllMatches();

        assertEquals(2, result.size());
        verify(matchRepository, times(1)).findAll();
    }

    @Test
    void saveMatch_shouldSaveMatch() {
        Match match = Match.builder().build();
        matchService.saveMatch(match);

        verify(matchRepository, times(1)).save(match);
    }

    private MatchDTO mockMatchDTO() {
        MatchDTO matchDTO = new MatchDTO();

        // Player 1
        PlayerPairing player1 = new PlayerPairing();
        PlayerPairing.User user1 = new PlayerPairing.User();
        user1.setId("p1");
        user1.setFirstName("Alice");
        user1.setLastName("Smith");
        player1.setUser(user1);
        player1.setFaction(Faction.ADEPTA_SORORITAS.getDisplayName());

        // Player 2
        PlayerPairing player2 = new PlayerPairing();
        PlayerPairing.User user2 = new PlayerPairing.User();
        user2.setId("p2");
        user2.setFirstName("Bob");
        user2.setLastName("Johnson");
        player2.setUser(user2);
        player2.setFaction(Faction.ADEPTUS_MECHANICUS.getDisplayName());

        // Player1Game y Player2Game
        MatchDTO.Player1Game player1Game = new MatchDTO.Player1Game();
        player1Game.setResult(1);  // Victoria
        player1Game.setPoints(90);

        MatchDTO.Player1Game player2Game = new MatchDTO.Player1Game();
        player2Game.setResult(0);  // Derrota
        player2Game.setPoints(60);

        // Seteo en el DTO principal
        matchDTO.setPlayer1(player1);
        matchDTO.setPlayer2(player2);
        matchDTO.setPlayer1Game(player1Game);
        matchDTO.setPlayer2Game(player2Game);

        return matchDTO;
    }


    @Test
    void testBulkMatches_multipleRounds_shouldProcessCorrectly() {
        // Arrange
        String eventId = "event123";
        when(bcpClient.getNumberOfRounds(eventId)).thenReturn(2);

        MatchDTO matchDTO = mockMatchDTO();

        when(bcpClient.getPairings(eq(eventId), anyInt()))
                .thenReturn(List.of(matchDTO));

        Player player1 = Player.builder().id("p1").matchCount(0).factionsPlayed(new HashMap<>()).build();
        Player player2 = Player.builder().id("p2").matchCount(0).factionsPlayed(new HashMap<>()).build();

        when(playerRepository.findById("p1")).thenReturn(Optional.of(player1));
        when(playerRepository.findById("p2")).thenReturn(Optional.of(player2));

        when(glickoRatingService.updateRatingsBulk(any(), any())).thenAnswer(inv -> inv.getArgument(0));
        when(playerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        matchService.bulkMatches(eventId);

        // Assert
        verify(matchRepository).saveAll(anyList());
        verify(playerRepository, times(2)).save(any());
    }

    @Test
    void testBulkMatches_playerNotFound_shouldThrowException() {
        String eventId = "eventWithMissingPlayer";
        when(bcpClient.getNumberOfRounds(eventId)).thenReturn(1);
        MatchDTO matchDTO = mockMatchDTO();
        when(bcpClient.getPairings(eventId, 1)).thenReturn(List.of(matchDTO));

        when(playerRepository.findById("p1")).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> matchService.bulkMatches(eventId));
    }


    @Test
    void testBulkMatches_invalidFaction_shouldThrowException() {
        String eventId = "eventInvalidFaction";
        when(bcpClient.getNumberOfRounds(eventId)).thenReturn(1);

        MatchDTO matchDTO = mockMatchDTO();
        matchDTO.getPlayer1().setFaction("INVALID_FACTION");

        when(bcpClient.getPairings(eventId, 1)).thenReturn(List.of(matchDTO));
        Player dummy = Player.builder().id("p1").matchCount(0).factionsPlayed(new HashMap<>()).build();
        when(playerRepository.findById(anyString())).thenReturn(Optional.of(dummy));

        assertThrows(IllegalArgumentException.class, () -> matchService.bulkMatches(eventId));
    }



    @Test
    void testDeleteAllMatches_shouldCallRepository() {
        matchService.deleteAllMatches();
        verify(matchRepository).deleteAll();
    }


    @Test
    void testGetAllMatches_shouldReturnAll() {
        List<Match> expected = List.of(new Match(), new Match());
        when(matchRepository.findAll()).thenReturn(expected);
        List<Match> result = matchService.getAllMatches();
        assertEquals(2, result.size());
    }


}
