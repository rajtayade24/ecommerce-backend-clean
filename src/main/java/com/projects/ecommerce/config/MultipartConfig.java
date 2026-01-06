package com.projects.ecommerce.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10));      // max single file
        factory.setMaxRequestSize(DataSize.ofMegabytes(20));   // total request
        factory.setFileSizeThreshold(DataSize.ofKilobytes(512)); // optional
        return factory.createMultipartConfig();
    }
}
