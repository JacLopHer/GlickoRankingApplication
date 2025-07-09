package com.example.RankingApplication.repository.impl;

import com.example.RankingApplication.model.Player;
import com.example.RankingApplication.repository.PlayerRepositoryCustom;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PlayerRepositoryCustomImpl<T extends Player> implements PlayerRepositoryCustom<T> {

    private final MongoTemplate mongoTemplate;

    public PlayerRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Player> findAll(Class<? extends Player> clazz) {
        return mongoTemplate.findAll(clazz).stream().filter(Objects::nonNull)
                .map(Player.class::cast)
                .toList();
    }

    @Override
    public boolean existsById(String id, Class<? extends T> clazz) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.exists(query, clazz);
    }


    @Override
    public void saveAll(List<Player> players) {
        for (Player player : players) {
            mongoTemplate.save(player);
        }
    }

    @Override
    public void deleteAll(Class<? extends Player> clazz) {
        mongoTemplate.remove(new Query(), clazz);
    }

    @Override
    public void deleteById(String id, Class<? extends Player> clazz) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, clazz);
    }

    @Override
    public Optional<Player> findById(String id, Class<? extends Player> clazz) {
        Player player = mongoTemplate.findById(id, clazz);
        return Optional.ofNullable(player);
    }

    @Override
    public void save(T player) {
        mongoTemplate.save(player);
    }
}
