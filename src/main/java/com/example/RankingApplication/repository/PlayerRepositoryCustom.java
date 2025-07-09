package com.example.RankingApplication.repository;

import com.example.RankingApplication.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepositoryCustom<T extends Player> {

    List<Player> findAll(Class<? extends Player> clazz);

    boolean existsById(String id, Class<? extends T> clazz);

    void saveAll(List<Player> players);

    void deleteAll(Class<? extends Player> clazz);

    void deleteById(String id, Class<? extends Player> clazz);

    Optional<Player> findById(String id, Class<? extends Player> clazz);

    void save(T player);
}
