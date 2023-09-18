package com.thoughtworks.problem3application.application.interfaces;

import java.util.List;

import com.thoughtworks.problem3application.application.dtos.request.AuthenticateRequestDTO;
import com.thoughtworks.problem3application.application.dtos.request.UserRequestCreateDTO;
import com.thoughtworks.problem3application.application.dtos.request.UserRequestUpdateDTO;
import com.thoughtworks.problem3application.application.dtos.response.AuthenticateResponseDTO;
import com.thoughtworks.problem3application.application.dtos.response.UserResponseDTO;

public interface UserApplicationService {
	UserResponseDTO create(UserRequestCreateDTO user);

	List<UserResponseDTO> getAll();

	UserResponseDTO getById(Long id);

	UserResponseDTO update(UserRequestUpdateDTO user);

	UserResponseDTO delete(Long id);

	AuthenticateResponseDTO authenticate(AuthenticateRequestDTO dto);
}
