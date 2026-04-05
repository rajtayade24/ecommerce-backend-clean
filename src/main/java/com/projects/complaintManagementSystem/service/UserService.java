package com.projects.complaintManagementSystem.service;

import com.projects.complaintManagementSystem.dto.AddressDto;
import com.projects.complaintManagementSystem.dto.UserDto;
import com.projects.complaintManagementSystem.dto.request.CreateUserDto;
import com.projects.complaintManagementSystem.dto.request.LoginRequest;
import com.projects.complaintManagementSystem.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
// import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserDto createUser(CreateUserDto userDto);

    UserDto login(LoginRequest dto);

    AddressDto addAddresses(AddressDto request);

    UserDto me(Authentication authentication);

    List<AddressDto> getAddresses();

    User getCurrentUser();

    Long getAllUsers();

    Page<UserDto> getUsers(String search, Boolean active, Pageable pageable);

    UserDto getUserById(Long id);

//    List<String> suggestKeywords(String q, int limit);

    // ResponseEntity<UserDto> handleOAuth2LoginRequest(OAuth2User oAuth2User,
    // String registrationId);

//    String uploadImage(@Valid MultipartFile image);
}
