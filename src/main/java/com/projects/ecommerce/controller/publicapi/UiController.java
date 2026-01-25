package com.projects.ecommerce.controller.publicapi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {
    @GetMapping("/")
    public String forwardIndex() {
        return "forward:/index.html";
    }
}