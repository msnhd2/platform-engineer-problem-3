package com.thoughtworks.problem3application.functional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.thoughtworks.problem3application.application.dtos.request.AuthenticateRequestDTO;
import com.thoughtworks.problem3application.application.dtos.request.UserRequestCreateDTO;
import com.thoughtworks.problem3application.application.dtos.request.UserRequestUpdateDTO;
import com.thoughtworks.problem3application.application.dtos.response.AuthenticateResponseDTO;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserFunctionalTest {
	@Autowired
	protected MockMvc mock;

	@Autowired
	protected ObjectMapper objectMapper;

	@Test
	void shouldRegisterAUserAndReturnStatusOK() throws Exception {

		// Arrange
		Faker faker = new Faker();
		String name = faker.name().fullName();
		String email = faker.internet().emailAddress();
		String password = faker.internet().password();
		UserRequestCreateDTO request = new UserRequestCreateDTO(name, email, password);
		String objPost = objectMapper.writeValueAsString(request);

		// Act
		mock.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objPost))
				.andExpect(status().isCreated());

		// Assert
	}

	@Test
	void shouldRegisterAUserAndUpdatedAndReturnStatusOK() throws Exception {

		// Arrange
		Faker faker = new Faker();
		String name = faker.name().fullName();
		String email = faker.internet().emailAddress();
		String password = faker.internet().password();
		UserRequestCreateDTO request = new UserRequestCreateDTO(name, email, password);
		String objPost = objectMapper.writeValueAsString(request);

		// Act
		mock.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objPost))
				.andExpect(status().isCreated());

		AuthenticateRequestDTO auth = new AuthenticateRequestDTO();
		auth.setEmail(email);
		auth.setPassword(password);
		String objAuth = objectMapper.writeValueAsString(auth);
		MvcResult result = mock.perform(post("/api/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objAuth))
				.andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString();
		AuthenticateResponseDTO response = objectMapper.readValue(content, AuthenticateResponseDTO.class);

		UserRequestUpdateDTO requestUpdate = new UserRequestUpdateDTO();
		requestUpdate.setId(Long.valueOf(response.getId()));
		requestUpdate.setName("name updated");
		String objUpdate = objectMapper.writeValueAsString(requestUpdate);

		// Assert
		mock.perform(put("/api/users")
				.header("Authorization", "Bearer " + response.getAccessToken())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objUpdate))
				.andExpect(status().isOk());
	}

	@Test
	void shouldRegisterAUserAndDeleteAndReturnStatusOK() throws Exception {

		// Arrange
		Faker faker = new Faker();
		String name = faker.name().fullName();
		String email = faker.internet().emailAddress();
		String password = faker.internet().password();
		UserRequestCreateDTO request = new UserRequestCreateDTO(name, email, password);
		String objPost = objectMapper.writeValueAsString(request);

		// Act
		mock.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objPost))
				.andExpect(status().isCreated());

		AuthenticateRequestDTO auth = new AuthenticateRequestDTO();
		auth.setEmail(email);
		auth.setPassword(password);
		String objAuth = objectMapper.writeValueAsString(auth);
		MvcResult result = mock.perform(post("/api/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objAuth))
				.andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString();
		AuthenticateResponseDTO response = objectMapper.readValue(content, AuthenticateResponseDTO.class);

		// Assert
		mock.perform(delete("/api/users/".concat(response.getId()))
				.header("Authorization", "Bearer " + response.getAccessToken())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	void shouldRegisterAUserAndGetAllAndReturnStatusOK() throws Exception {

		// Arrange
		Faker faker = new Faker();
		String name = faker.name().fullName();
		String email = faker.internet().emailAddress();
		String password = faker.internet().password();
		UserRequestCreateDTO request = new UserRequestCreateDTO(name, email, password);
		String objPost = objectMapper.writeValueAsString(request);

		// Act
		mock.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objPost))
				.andExpect(status().isCreated());

		AuthenticateRequestDTO auth = new AuthenticateRequestDTO();
		auth.setEmail(email);
		auth.setPassword(password);
		String objAuth = objectMapper.writeValueAsString(auth);
		MvcResult result = mock.perform(post("/api/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objAuth))
				.andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString();
		AuthenticateResponseDTO response = objectMapper.readValue(content, AuthenticateResponseDTO.class);

		// Assert
		mock.perform(get("/api/users")
				.header("Authorization", "Bearer " + response.getAccessToken())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void shouldRegisterAUserAndGetByIdAndReturnStatusOK() throws Exception {

		// Arrange
		Faker faker = new Faker();
		String name = faker.name().fullName();
		String email = faker.internet().emailAddress();
		String password = faker.internet().password();
		UserRequestCreateDTO request = new UserRequestCreateDTO(name, email, password);
		String objPost = objectMapper.writeValueAsString(request);

		// Act
		mock.perform(post("/api/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objPost))
				.andExpect(status().isCreated());

		AuthenticateRequestDTO auth = new AuthenticateRequestDTO();
		auth.setEmail(email);
		auth.setPassword(password);
		String objAuth = objectMapper.writeValueAsString(auth);
		MvcResult result = mock.perform(post("/api/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objAuth))
				.andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString();
		AuthenticateResponseDTO response = objectMapper.readValue(content, AuthenticateResponseDTO.class);

		// Assert
		mock.perform(get("/api/users/".concat(response.getId()))
				.header("Authorization", "Bearer " + response.getAccessToken())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
