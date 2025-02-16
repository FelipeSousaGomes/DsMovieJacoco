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
import com.devsuperior.dsmovie.tests.ScoreFactory;
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

	private Long existindId, nonExistindId;


	private ScoreEntity score;
	private ScoreDTO scoreDTO;

	@BeforeEach
	public void setUp() {
		existindId = 1L;
		nonExistindId = 2L;
		score = ScoreFactory.createScoreEntity();
		scoreDTO = new ScoreDTO(score);
		user = UserFactory.createUserEntity();
		MovieEntity movie = MovieFactory.createMovieEntity();

		Mockito.when(userService.authenticated()).thenReturn(user);

		Mockito.when(movieRepository.findById(existindId)).thenReturn(Optional.of(movie));
		Mockito.when(movieRepository.findById(nonExistindId)).thenReturn(Optional.empty());
		Mockito.when(repository.save(score)).thenReturn(score);
		Mockito.doThrow(ResourceNotFoundException.class).when(movieRepository).findById(nonExistindId);
		Mockito.doThrow(ResourceNotFoundException.class).when(movieRepository).save(movie);


		Mockito.when(repository.saveAndFlush(any())).thenReturn(score);


		Mockito.when(movieRepository.save(any())).thenReturn(movie);
	}


	
	@Test
	public void saveScoreShouldReturnMovieDTO() {

		MovieDTO movieDTO = service.saveScore(scoreDTO);

		Assertions.assertNotNull(movieDTO);
		Assertions.assertEquals(scoreDTO.getMovieId(), movieDTO.getId());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		scoreDTO.setMovieId(nonExistindId);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.saveScore(scoreDTO));
	}
}
