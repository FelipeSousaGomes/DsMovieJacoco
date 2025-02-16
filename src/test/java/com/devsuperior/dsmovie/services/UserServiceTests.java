package com.devsuperior.dsmovie.services;


import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;

import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil userUtil;

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		String username = "maria";
		UserEntity mockUser = new UserEntity();
		mockUser.setUsername(username);


		Mockito.when(userUtil.getLoggedUsername()).thenReturn(username);
		Mockito.when(repository.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));


		UserEntity result = service.authenticated();


		assertNotNull(result);
		assertEquals(username, result.getUsername());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		String username = "nonexistent_user";


		Mockito.when(userUtil.getLoggedUsername()).thenReturn(username);
		Mockito.when(repository.findByUsername(username)).thenReturn(java.util.Optional.empty());


		assertThrows(UsernameNotFoundException.class, () -> service.authenticated());
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

		String username = "alex";


		List<UserDetailsProjection> mockUserDetails = UserDetailsFactory.createCustomClientUser(username);

		Mockito.when(repository.searchUserAndRolesByUsername(username))
				.thenReturn(mockUserDetails);


		UserDetails result = service.loadUserByUsername(username);


		assertNotNull(result);
		assertEquals(username, result.getUsername());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		// Arrange
		String username = "nonexistent_user";

		// Mock repository to return empty list
		Mockito.when(repository.searchUserAndRolesByUsername(username)).thenReturn(new java.util.ArrayList<>());

		// Act and Assert
		assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(username));
	}
}

