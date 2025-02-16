package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

	@InjectMocks
	private ScoreService service;

	@Mock
	private ScoreRepository repository;

	@Mock
	private UserService userService;

	@Mock
	private MovieRepository movieRepository;

	private UserEntity user;
	private MovieEntity movie;
	private Long existingId, nonExistingId;
	private ScoreDTO scoreDTO;

	@BeforeEach
	public void setUp() {
		existingId = 1L;
		nonExistingId = 2L;

		user = UserFactory.createUserEntity();
		movie = MovieFactory.createMovieEntity();


		ScoreEntity score1 = new ScoreEntity();
		score1.setMovie(movie);
		score1.setUser(user);
		score1.setValue(4.0);

		ScoreEntity score2 = new ScoreEntity();
		score2.setMovie(movie);
		score2.setUser(user);
		score2.setValue(2.0);

		movie.getScores().add(score1);
		movie.getScores().add(score2);


		scoreDTO = new ScoreDTO(score1);
	}
	@Test
	public void saveScoreShouldReturnMovieDTO() {

		scoreDTO.setMovieId(existingId);
		scoreDTO.setScore(5.0);

		Mockito.when(movieRepository.findById(existingId)).thenReturn(Optional.of(movie));


		ScoreEntity newScore = new ScoreEntity();
		newScore.setMovie(movie);
		newScore.setUser(user);
		newScore.setValue(5.0);
		movie.getScores().add(newScore);


		movie.setScore((movie.getScore() * movie.getScores().size() + newScore.getValue()) / (movie.getScores().size() + 1));
		movie.setCount(movie.getScores().size());


		Mockito.when(movieRepository.save(Mockito.any(MovieEntity.class))).thenReturn(movie);


		MovieDTO movieDTO = service.saveScore(scoreDTO);


		Assertions.assertNotNull(movieDTO);



		Mockito.verify(movieRepository).save(Mockito.any(MovieEntity.class));
	}

	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		scoreDTO.setMovieId(nonExistingId);


		Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.saveScore(scoreDTO));
	}

}
