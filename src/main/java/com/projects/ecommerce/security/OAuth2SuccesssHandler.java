//package com.projects.ecommerce.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.projects.ecommerce.dto.UserDto;
//import com.projects.ecommerce.service.UserService;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class OAuth2SuccesssHandler implements AuthenticationSuccessHandler {
//
//    private final UserService userService;
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//
//        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//
//        String registrationId = token.getAuthorizedClientRegistrationId();
//
//        ResponseEntity<UserDto> dto = userService.handleOAuth2LoginRequest(oAuth2User, registrationId);
//
//        response.setStatus(dto.getStatusCode().value()); // return ok (status code)
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);    // return josn in frontend
//        response.getWriter().write(objectMapper.writeValueAsString(dto.getBody()));
//    }
//}
