package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;
	private Long existingMovieId,nonExistingMovieId,dependentId;
	private PageImpl<MovieEntity> movies;
	private MovieEntity movie;
	private MovieDTO movieDTO;
	private String title;

	@BeforeEach
	public void setUp() {
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		dependentId = 3L;
		title = "Test Movie";
		movie = MovieFactory.createMovieEntity();
		movieDTO = new MovieDTO(movie);
		movies = new PageImpl<>(List.of(movie));


		Mockito.when(repository.searchByTitle(any(),any())).thenReturn(movies);
		Mockito.when(repository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		Mockito.when(repository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
		Mockito.when(repository.save(any())).thenReturn(movie);
		Mockito.when(repository.getReferenceById(existingMovieId)).thenReturn(movie);
		Mockito.when(repository.getReferenceById(nonExistingMovieId)).thenReturn(null);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingMovieId);
		Mockito.when(repository.existsById(existingMovieId)).thenReturn(true);
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
		Mockito.when(repository.existsById(nonExistingMovieId)).thenReturn(false);
		Mockito.doNothing().when(repository).deleteById(existingMovieId);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingMovieId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

	}

	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<MovieDTO> page = service.findAll(title,pageable);

		Assertions.assertNotNull(page);
		Assertions.assertEquals(title,page.getContent().get(0).getTitle());


	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		MovieDTO movieDTO = service.findById(existingMovieId);
		Assertions.assertNotNull(movieDTO);
		Assertions.assertEquals(title,movieDTO.getTitle());

	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingMovieId));

	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
		MovieDTO movie = service.insert(movieDTO);

		Assertions.assertEquals(title,movie.getTitle());
		Assertions.assertEquals(movie.getId(),movieDTO.getId());


	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		MovieDTO movie = service.update(existingMovieId, movieDTO);
		Assertions.assertEquals(title,movie.getTitle());
		Assertions.assertEquals(movie.getId(),movieDTO.getId());


	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingMovieId, movieDTO));
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> service.delete(existingMovieId));

	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingMovieId));
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentId));
	}
}
