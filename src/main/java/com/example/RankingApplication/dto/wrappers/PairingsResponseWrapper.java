package com.example.RankingApplication.dto.wrappers;

import com.example.RankingApplication.dto.MatchDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PairingsResponseWrapper {
    List<MatchDTO> active;
}
