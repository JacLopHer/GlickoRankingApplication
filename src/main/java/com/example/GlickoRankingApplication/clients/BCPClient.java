package com.example.GlickoRankingApplication.clients;

import com.example.GlickoRankingApplication.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Component
public class BCPClient {

    private final WebClient webClient;
    private String authToken;

    public BCPClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://newprod-api.bestcoastpairings.com/v1")
                .defaultHeader("client-id", "web-app")
                .build();
    }

    public void authenticate() {
        var requestBody = new AuthRequest("kanarias.open@gmail.com", "Tito&2026");

        try {
            AuthResponse response = webClient.post()
                    .uri("/users/signin")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AuthResponse.class)
                    .block();

            assert response != null;
            this.authToken = response.accessToken();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to authenticate: " + e.getResponseBodyAsString(), e);
        }
    }

    public Integer getNumberOfRounds(String eventId) {
        return webClient.get()
                .uri("/events/{eventId}", eventId)
                .headers(headers -> headers.setBearerAuth(authToken))
                .retrieve()
                .bodyToMono(EventDTO.class)
                .map(EventDTO::rounds) // Asumiendo que Event tiene un método getRounds que devuelve el número de rondas
                .doOnTerminate(() -> System.out.println("Request completed"))
                .block(); // block para esperar la respuesta de manera sincrónica
    }

    public List<PairingJson> getPairings(String eventId, int round) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/events/{eventId}/pairings")
                        .queryParam("pairingType", "Pairing")
                        .queryParam("round", round)
                        .build(eventId))
                .headers(headers -> headers.setBearerAuth(authToken))
                .retrieve()
                .bodyToFlux(PairingJson.class)
                .collectList()
                .block();
    }


    public List<PlayerJson> getPlayers(String eventId) {
        authenticate();

        ObjectMapper objectMapper = new ObjectMapper();

        // Usamos exchange() para mayor control sobre la respuesta
        List<PlayerJson> response = webClient.get()
                .uri("/events/{eventId}/players?placings=true", eventId)
                .headers(headers -> headers.setBearerAuth(authToken))
                .retrieve()
                .bodyToMono(PlacingsResponseWrapper.class)
                .map(PlacingsResponseWrapper::getActive)
                .doOnTerminate(() -> System.out.println("Request Get Players from BCP completed"))
                .block();  // Esto bloquea y obtiene la respuesta

        return response.size() > 0 ? response : new ArrayList<>();
    }

    // Clases auxiliares
    private record AuthRequest(String username, String password) {}

    private record AuthResponse(String accessToken) {
        public String accessToken() {
            return accessToken;  // Solo devolvemos el accessToken
        }
    }
}
