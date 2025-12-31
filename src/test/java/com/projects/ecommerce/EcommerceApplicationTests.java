//package com.projects.ecommerce;
//
//import com.projects.ecommerce.entity.User;
//import com.projects.ecommerce.enums.RoleType;
//import com.projects.ecommerce.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Set;
//
//@SpringBootTest
//class EcommerceApplicationTests {
//
//    @Autowired
//    private UserRepository userRepository;
//
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//
//    @Test
//
//    void contextLoads() {
//    }
//
////    @Test
////    void addAdmin() {
////        User user = new User();
////        user.setEmail("admin@123.com");
////        user.setPassword(passwordEncoder().encode("Admin@123")); // In real apps, always hash passwords!
////        user.setName("Admin User");
////        user.setAddress("123 Admin Street");
////        user.setCity("Pune");
////        user.setState("Maharashtra");
////        user.setPincode("123456");
////        user.setRoles(Set.of(RoleType.ADMIN));
////        user.setActive(true);
////
////        userRepository.save(user);
////    }
//
//}
