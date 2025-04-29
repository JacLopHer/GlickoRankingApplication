package com.example.GlickoRankingApplication.dto.bcp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserJson {
    private String id;
    private String firstName;
    private String lastName;
}

