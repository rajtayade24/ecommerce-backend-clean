//package com.projects.ecommerce;
//
//import com.projects.ecommerce.entity.User;
//import com.projects.ecommerce.repository.UserRepository;
//import com.projects.ecommerce.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.List;
//
//@SpringBootTest
//public class UserTest {
//
//    @Autowired
//    private UserService searchService;
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private AuthenticationManager manager;
//
//    @Test
//    public void testAllUsers() {
//        List<User> users = userRepository.findAll();
//        System.out.println(users);
//    }
//
//    @Test
//    public void printAuth() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Authentication user: "+auth);
//    }
//
//    @Test
//    void test() {
//        List<String> lines = searchService.suggestKeywords("cas", 4);
//
//        lines.forEach(l -> System.out.println("line is: " + l));
//    }
//
//
//
//}
