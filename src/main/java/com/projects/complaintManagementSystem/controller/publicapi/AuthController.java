package com.projects.complaintManagementSystem.controller.publicapi;

import com.projects.complaintManagementSystem.dto.AddressDto;
import com.projects.complaintManagementSystem.dto.UserDto;
import com.projects.complaintManagementSystem.dto.request.CreateUserDto;
import com.projects.complaintManagementSystem.dto.request.LoginRequest;
import com.projects.complaintManagementSystem.service.UserService;
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
        "*"
})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<UserDto> createNewUser(@RequestBody CreateUserDto userDto) {
        UserDto created = userService.createUser(userDto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/auth/login")
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

//    @PostMapping("/verify-mobile")
//    public ResponseEntity<?> verifyMobile(@RequestBody OtpRequest req) {
//
//        boolean valid = otpService.verify(req.getMobile(), req.getOtp());
//
//        if (!valid) {
//            throw new RuntimeException("Invalid OTP");
//        }
//
//        User user = userRepository.findByMobile(req.getMobile())
//                .orElseThrow();
//
//        user.setMobileVerified(true);
//
//        // Activate if verified
//        if (user.isMobileVerified() || user.isEmailVerified()) {
//            user.setAccountStatus(AccountStatus.ACTIVE);
//        }
//
//        userRepository.save(user);
//
//        return ResponseEntity.ok("Mobile verified");
//    }
//
//    @PostMapping("/verify-mobile")
//    public ResponseEntity<?> verifyMobile(@RequestBody OtpRequest req) {
//
//        boolean valid = otpService.verify(req.getMobile(), req.getOtp());
//
//        if (!valid) {
//            throw new RuntimeException("Invalid OTP");
//        }
//
//        User user = userRepository.findByMobile(req.getMobile())
//                .orElseThrow();
//
//        user.setMobileVerified(true);
//
//        // Activate if verified
//        if (user.isMobileVerified() || user.isEmailVerified()) {
//            user.setAccountStatus(AccountStatus.ACTIVE);
//        }
//
//        userRepository.save(user);
//
//        return ResponseEntity.ok("Mobile verified");
//    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getUserAddresses() {
        return ResponseEntity.ok(userService.getAddresses());
    }
}
