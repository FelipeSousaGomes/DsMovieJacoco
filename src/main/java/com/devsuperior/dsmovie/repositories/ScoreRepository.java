package com.devsuperior.dsmovie.repositories;

import com.devsuperior.dsmovie.entities.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.ScoreEntityPK;

import java.util.Optional;

public interface ScoreRepository extends JpaRepository<ScoreEntity, ScoreEntityPK> {

}