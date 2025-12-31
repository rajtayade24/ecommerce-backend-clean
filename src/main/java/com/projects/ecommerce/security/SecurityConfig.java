package com.projects.ecommerce.security;

import com.projects.ecommerce.config.AppConfig;
import com.projects.ecommerce.enums.RoleType;
import com.projects.ecommerce.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final AppConfig appConfig;
//    private final OAuth2SuccesssHandler oAuth2SuccesssHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight
                                .requestMatchers("/", "/auth/login", "/auth/signup").permitAll()
                                .requestMatchers("/otp/send", "/otp/verify").permitAll()
                                .requestMatchers("/public/**", "/uploads/**").permitAll()
                                .requestMatchers("/search/suggestions/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/categories/**", "/products/**").permitAll()
                                .requestMatchers("/payment-success/**", "/payment-cancel").permitAll()
                                .requestMatchers("/admin/**").hasRole(RoleType.ADMIN.name()) // only admin can POST
//
                                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class

//                )
//                .oauth2Client(Customizer.withDefaults())
//
//                // OAuth2 login
//                .oauth2Login(oauth2 ->
//                        oauth2.
//                                failureHandler((AuthenticationFailureHandler) (request, response, exception) -> {
//                                    log.error("oauth2 error" + exception.getMessage());
//                                })
//                                .successHandler(oAuth2SuccesssHandler)
                );

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); // inject in constructor
        provider.setPasswordEncoder(appConfig.passwordEncoder());
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5500"); // frontend
        configuration.addAllowedOrigin("http://127.0.0.1:5500");
//        configuration.addAllowedOrigin("*"); // optional for dev
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true); // if sending cookies / auth headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
