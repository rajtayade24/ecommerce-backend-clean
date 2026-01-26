package com.projects.ecommerce.controller.admin;


import com.projects.ecommerce.dto.CategoryDto;
import com.projects.ecommerce.dto.request.RequestCategoryDto;
import com.projects.ecommerce.service.OrderService;
import com.projects.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

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
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/revenue")
    public BigDecimal getRevenue() {
        return orderService.getTotalRevenue();
    }


    @PostMapping("/logo")
    public ResponseEntity<String> uploadImg(@RequestPart("image") @Valid MultipartFile image) {
        String img = userService.uploadImage(image);
        return ResponseEntity.ok(img);
    }

}
