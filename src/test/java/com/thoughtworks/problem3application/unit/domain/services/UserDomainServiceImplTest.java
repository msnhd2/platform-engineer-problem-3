package com.thoughtworks.problem3application.unit.domain.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.thoughtworks.problem3application.domain.entities.User;
import com.thoughtworks.problem3application.domain.services.UserDomainServiceImpl;
import com.thoughtworks.problem3application.helpers.UserBuilder;
import com.thoughtworks.problem3application.infrastructure.components.MD5Component;
import com.thoughtworks.problem3application.infrastructure.repository.repositories.UserRepository;
import com.thoughtworks.problem3application.infrastructure.security.TokenCreator;

@ExtendWith(SpringExtension.class)
public class UserDomainServiceImplTest {

	@InjectMocks
	private UserDomainServiceImpl userDomainService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MD5Component md5Component;

	@Mock
	private TokenCreator tokenCreator;

	@Test
	void shouldCreateUser() {
		// Arrange
		User user = new UserBuilder().builder();
		when(userRepository.save(any(User.class))).thenReturn(user);

		// Act
		User userCreated = userDomainService.create(user);

		// Assert
		verify(md5Component, times(1)).encrypt(anyString());
		verify(userRepository, times(1)).save(user);
		assertEquals(user, userCreated);
	}

	@Test
	void shouldReturnAnExceptionWhenCreatingAUserWithTheEmailAlreadyRegistered() {
		// Arrange
		User user = new UserBuilder().builder();
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

		// Act
		Exception exception = assertThrows(IllegalArgumentException.class, () -> userDomainService.create(user));

		// Assert
		assertEquals(IllegalArgumentException.class, exception.getClass());
		assertEquals("E-mail already registered.", exception.getMessage());
		verify(md5Component, times(0)).encrypt(anyString());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	void shouldUpdateUser() {
		// Arrange
		User user = new UserBuilder().builder();
		long id = user.getId();
		when(userRepository.findById(id)).thenReturn(Optional.of(user));

		// Act
		user.setName("new name user");
		userDomainService.update(user);
		Optional<User> userUpdate = userRepository.findById(id);

		// Assert
		verify(userRepository, times(1)).save(user);
		assertEquals(user, userUpdate.get());
	}

	@Test
	void shouldDeleteUser() {
		// Arrange
		User user = new UserBuilder().builder();
		long id = user.getId();
		when(userRepository.findById(id)).thenReturn(Optional.of(user));

		// Act
		userDomainService.delete(user);

		// Assert
		verify(userRepository, times(1)).delete(user);
	}

	@Test
	void shouldGetByIdUser() {
		// Arrange
		User user = new UserBuilder().builder();
		long id = user.getId();
		when(userRepository.findById(id)).thenReturn(Optional.of(user));

		// Act
		Optional<User> userGet = userDomainService.getById(id);

		// Assert
		verify(userRepository, times(1)).findById(id);
		assertEquals(id, userGet.get().getId());
		assertEquals(user, userGet.get());
	}

	@Test
	void shouldGetAllUser() {
		// Arrange
		User user1 = new UserBuilder().builder();
		User user2 = new UserBuilder().builder();
		User user3 = new UserBuilder().builder();
		User user4 = new UserBuilder().builder();
		when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3, user4));

		// Act
		List<User> users = userDomainService.getAll();

		// Assert
		verify(userRepository, times(1)).findAll();
		assertEquals(4, users.size());
	}

	@Test
	void shouldAuthenticateARegisteredUser() {
		// Arrange
		User user = new UserBuilder().builder();
		String email = user.getEmail();
		String password = user.getPassword();
		when(md5Component.encrypt(anyString())).thenReturn(password);
		when(userRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.of(user));
		when(tokenCreator.generateToken(email)).thenReturn(UUID.randomUUID().toString());

		// Act
		User userAuth = userDomainService.authenticate(email, password);

		// Assert
		verify(userRepository, times(1)).findByEmailAndPassword(email, password);
		assertNotNull(userAuth.getAccessToken());
	}

	@Test
	void shouldReturnAnExceptionWhenAuthenticatingAnUnregisteredUser() {
		// Arrange
		String email = "email@email";
		String password = "123qwe";
		when(md5Component.encrypt(anyString())).thenReturn(password);
		when(userRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.empty());

		// Act
		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> userDomainService.authenticate(email, password));

		// Assert
		assertEquals(IllegalArgumentException.class, exception.getClass());
		assertEquals("Access denied. User not found.", exception.getMessage());
		verify(md5Component, times(1)).encrypt(anyString());
		verify(tokenCreator, times(0)).generateToken(anyString());
	}

	@Test
	void shouldReturnTrueWhenUsersIsEquals() {
		User user1 = new User();
		user1.setId(1l);
		user1.setEmail("email@email");
		User user2 = new User();
		user2.setId(1l);
		user2.setEmail("email@email");

		String toStringUser1 = user1.toString();

		assertEquals(toStringUser1, user2.toString());
	}
}
