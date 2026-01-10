package com.projects.ecommerce.controller.publicapi;

import com.projects.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
public class UserController {
    private final UserService userService;

    @GetMapping("/search/suggestions")
    public ResponseEntity<List<String>> suggestions(@RequestParam("q") String q,
                                                    @RequestParam(value = "limit", defaultValue = "6") int limit) {
        return ResponseEntity.ok(userService.suggestKeywords(q, limit));
    }

    // Optional: accept JSON body { "search": "..." }
    @PostMapping("/search/suggestions")
    public ResponseEntity<List<String>> suggestionsPost(@RequestBody Map<String, String> body,
                                                        @RequestParam(value = "limit", defaultValue = "6") int limit) {
        String q = body.getOrDefault("search", "");
        return ResponseEntity.ok(userService.suggestKeywords(q, limit));
    }
}
