package com.projects.ecommerce.controller.admin;

import com.projects.ecommerce.dto.UserDto;
import com.projects.ecommerce.service.OrderService;
import com.projects.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService service;
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<UserDto> userPage = service.getUsers(search, active, pageable);
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countOrders() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> setUserActive(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body
    ) {
        Boolean active = body.get("active");

        if (active == null) {
            throw new IllegalArgumentException("active field is required");
        }

        UserDto updated = service.setUserActive(id, active);
        return ResponseEntity.ok(updated);
    }
}
