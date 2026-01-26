//package com.projects.ecommerce.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        // Serve index.html for root
//        registry.addViewController("/").setViewName("forward:/index.html");
//
//        // Serve index.html for all single-segment paths (like /products)
//        registry.addViewController("/{path:^(?!api$).*$}")
//                .setViewName("forward:/index.html");
//
//        // Serve index.html for all deeper paths (like /products/123)
//        registry.addViewController("/{path:^(?!api$).*$}/{subpath:[^\\.]*}")
//                .setViewName("forward:/index.html");
//    }
//}
