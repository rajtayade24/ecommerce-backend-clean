package com.projects.ecommerce.service;

import com.projects.ecommerce.dto.AddressDto;
import com.projects.ecommerce.dto.UserDto;
import com.projects.ecommerce.dto.request.AddAddressRequest;
import com.projects.ecommerce.dto.request.LoginRequest;
import com.projects.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto login(LoginRequest dto);

     AddressDto addAddresses(AddAddressRequest request);

     UserDto me(Authentication authentication);

//    String upload(MultipartFile file);

    void deleteFile(String filePath);

    List<AddressDto> getAddresses(Long id);

    User getCurrentUser();

    Long getAllUsers();

    Page<UserDto> getUsers(String search, Boolean active, Pageable pageable);

    UserDto getUserById(Long id);

    UserDto setUserActive(Long userId, boolean active);

     List<String> suggestKeywords(String q, int limit);

    ResponseEntity<UserDto> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId);
}
