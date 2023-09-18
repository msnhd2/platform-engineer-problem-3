package com.thoughtworks.problem3application.domain.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thoughtworks.problem3application.domain.entities.User;
import com.thoughtworks.problem3application.domain.interfaces.UserDomainService;
import com.thoughtworks.problem3application.infrastructure.components.MD5Component;
import com.thoughtworks.problem3application.infrastructure.repository.repositories.UserRepository;
import com.thoughtworks.problem3application.infrastructure.security.TokenCreator;

@Service
public class UserDomainServiceImpl implements UserDomainService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MD5Component md5Component;

	@Autowired
	private TokenCreator tokenCreator;

	@Override
	public User create(User user) {
		Optional<User> optional = userRepository.findByEmail(user.getEmail());
		if (optional.isPresent()) {
			throw new IllegalArgumentException("E-mail already registered.");
		}
		user.setPassword(md5Component.encrypt(user.getPassword()));

		return userRepository.save(user);
	}

	@Override
	public List<User> getAll() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> getById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public void update(User user) {
		userRepository.save(user);
	}

	@Override
	public void delete(User user) {
		userRepository.delete(user);
	}

	@Override
	public User authenticate(String email, String password) {
		Optional<User> optional = userRepository.findByEmailAndPassword(email, md5Component.encrypt(password));
		if (optional.isEmpty()) {
			throw new IllegalArgumentException("Access denied. User not found.");
		}

		User userGet = optional.get();

		userGet.setAccessToken(tokenCreator.generateToken(userGet.getEmail()));

		return userGet;
	}
}
