package com.thoughtworks.problem3application.helpers;

import com.github.javafaker.Faker;
import com.thoughtworks.problem3application.domain.entities.User;

public class UserBuilder {
	Faker faker = new Faker();
	User user;

	private long id = faker.random().nextLong(1);
	private String name = faker.name().fullName();
	private String email = faker.internet().emailAddress();
	private String password = faker.internet().password();
	private String accessToken = null;

	public User builder() {
		return new User(this.id, name, email, password, accessToken);
	}
}
