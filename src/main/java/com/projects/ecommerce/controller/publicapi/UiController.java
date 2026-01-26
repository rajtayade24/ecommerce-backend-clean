package com.projects.ecommerce.controller.publicapi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    @GetMapping({
            "/",
            "/products/**",
            "/categories/**",
            "/verify/**",
            "/cart/**",
            "/me/**",
            "/order/**",
            "/payment-success/**",
            "/about",
            "/feedbacks",
            "/term-service",
            "/privacy-policy",
            "/admin/**"
    })
    public String forward() {
        return "forward:/index.html";
    }

//    @GetMapping("/{path:[^\\.]*}") // ❌ This mapping only matches ONE path segment
//    public String forward() {
//        return "forward:/index.html";
//    }
//    public String forwardIndex() {
//        return "forward:/index.html";
//    }
}