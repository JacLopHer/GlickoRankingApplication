package com.example.RankingApplication.client;

import com.example.RankingApplication.config.BcpProperties;
import com.example.RankingApplication.dto.MatchDTO;
import com.example.RankingApplication.dto.bcp.EventDTO;
import com.example.RankingApplication.dto.bcp.PlayerPlayer;
import com.example.RankingApplication.dto.wrappers.PairingsResponseWrapper;
import com.example.RankingApplication.dto.wrappers.PlacingsResponseWrapper;
import com.example.RankingApplication.exceptions.BCPClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class BCPClient {

    private final WebClient webClient;
    private String authToken;
    private final BcpProperties bcpProperties;

    @Autowired
    public BCPClient(WebClient.Builder webClientBuilder, BcpProperties bcpProperties) {
        this.bcpProperties = bcpProperties;
        this.webClient = webClientBuilder
                .baseUrl("https://newprod-api.bestcoastpairings.com/v1")
                .defaultHeader("client-id", "web-app")
                .build();
    }

    public void authenticate() {
        var requestBody = new AuthRequest(bcpProperties.getUsername(), bcpProperties.getPassword());

        try {
            AuthResponse response = webClient.post()
                    .uri("/users/signin")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AuthResponse.class)
                    .block();

            if (response == null) {
                throw new BCPClientException("Authentication failed: response is null");
            }

            this.authToken = response.accessToken();
        } catch (WebClientResponseException e) {
            throw new BCPClientException("Failed to authenticate with BCP: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new BCPClientException("Unexpected error during BCP authentication", e);
        }
    }

    public EventDTO getEvent(String eventId) {
        authenticate();
        return webClient.get()
                .uri("/events/{eventId}/overview", eventId)
                .headers(headers -> headers.setBearerAuth(authToken))
                .retrieve()
                .bodyToMono(EventDTO.class)
                .doOnTerminate(() -> log.info("Fetched event {}", eventId))
                .block();
    }

    public List<MatchDTO> getPairings(String eventId, int round) {
        authenticate();
        return Objects.requireNonNull(webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/events/{eventId}/pairings")
                                .queryParam("pairingType", "Pairing")
                                .queryParam("round", round)
                                .build(eventId))
                        .headers(headers -> headers.setBearerAuth(authToken))
                        .retrieve()
                        .bodyToMono(PairingsResponseWrapper.class)
                        .block())
                .getActive();
    }


    public List<PlayerPlayer> getPlayers(String eventId) {
        authenticate();

        // Usamos exchange() para mayor control sobre la respuesta
        List<PlayerPlayer> response = webClient.get()
                .uri("/events/{eventId}/players?placings=true", eventId)
                .headers(headers -> headers.setBearerAuth(authToken))
                .retrieve()
                .bodyToMono(PlacingsResponseWrapper.class)
                .map(PlacingsResponseWrapper::getActive)
                .doOnTerminate(() -> log.info("Request Get Players from BCP completed"))
                .block();  // Esto bloquea y obtiene la respuesta

        assert response != null;
        return response.isEmpty() ? new ArrayList<>() : response;
    }

    // Clases auxiliares
    private record AuthRequest(String username, String password) {}

    private record AuthResponse(String accessToken) { }
}
