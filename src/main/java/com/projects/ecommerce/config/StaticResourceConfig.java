package com.projects.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // If you save files to a relative folder "uploads/" inside your project directory:
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/uploads/"); // note: "file:" protocol    must add / before upload

        // If you used absolute path "/uploads/" use:
        // registry.addResourceHandler("/uploads/**")
        //        .addResourceLocations("file:/uploads/");
    }
}

