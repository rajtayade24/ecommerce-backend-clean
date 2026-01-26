package com.projects.ecommerce.controller.publicapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@RestController
@RequiredArgsConstructor
public class HealthController {

//    @GetMapping("/")
//    public ResponseEntity<String> root() {
//        return ResponseEntity.ok("Server is running");
//    }

    @GetMapping("/api/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP");
    }
}
