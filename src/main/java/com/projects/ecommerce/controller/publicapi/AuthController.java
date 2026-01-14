package com.projects.ecommerce.controller.publicapi;

import com.projects.ecommerce.dto.AddressDto;
import com.projects.ecommerce.dto.UserDto;
import com.projects.ecommerce.dto.request.AddAddressRequest;
import com.projects.ecommerce.dto.request.LoginRequest;
import com.projects.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://localhost:8080",
        "http://127.0.0.1:5500",
        "http://localhost:5173",
        "http://10.91.2.29:5173",
        "http://10.91.2.29:5173/instagram-clone",
        "https://social-media-frontend-nbdo.vercel.app",
        "*"
})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> createNewUser(@RequestBody UserDto userDto) {
        UserDto created = userService.createUser(userDto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.me(authentication));
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getUserAddresses() {
        return ResponseEntity.ok(userService.getAddresses());
    }

    @PostMapping("/addresses/add")
    public ResponseEntity<AddressDto> addAddress(
            @RequestBody AddAddressRequest request) {

        return ResponseEntity.ok(userService.addAddresses(request));
    }

}
