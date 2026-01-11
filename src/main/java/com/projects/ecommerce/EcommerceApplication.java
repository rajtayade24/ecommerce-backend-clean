package com.projects.ecommerce;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import java.util.Arrays;

//@EnableSpringDataWebSupport(
//        pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
//)
@SpringBootApplication
public class EcommerceApplication {

    @Autowired
    private Environment env;

    @PostConstruct
    public void showEnv() {
        System.out.println("Active Profiles: " + Arrays.toString(env.getActiveProfiles()));
        System.out.println("DB_URL: " + env.getProperty("DB_URL"));
        System.out.println("DB_USERNAME: " + env.getProperty("DB_USERNAME"));
        System.out.println("PORT: " + env.getProperty("PORT"));
    }
	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

}
