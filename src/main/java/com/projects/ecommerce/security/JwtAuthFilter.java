package com.projects.ecommerce.security;

import com.projects.ecommerce.entity.User;
import com.projects.ecommerce.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.swing.plaf.UIResource;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
            log.debug("Authorization header: {}", request.getHeader("Authorization"));

            String path = request.getServletPath();

            if (path.equals("/auth/login") || path.equals("/auth/signup")) {
                filterChain.doFilter(request, response);
                return;
            }

            ///  not block sign up
            final String requestHeader = request.getHeader("Authorization");

            if (requestHeader == null || !requestHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = requestHeader.substring(7);
            log.debug("Extracted token (first 20 chars): {}", token.length() > 20 ? token.substring(0, 20) + "..." : token);
            String identifier = authUtil.getUsernameFromToken(token);
            log.debug("Token subject (identifier): {}", identifier);

            if (identifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository
                        .findByEmailOrMobile(identifier, identifier)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found for JWT identifier: " + identifier)
                        );

//                    UsernamePasswordAuthenticationToken authToken =
//                            new UsernamePasswordAuthenticationToken(
//                                    user,
//                                    null,
//                                    user.getAuthorities()
//                            );
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );
                
                //if i sign in and request for addresses then the backend is unable to get user
                authToken.setDetails(  // block is exactly the reason
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
