package com.projects.complaintManagementSystem.security;

import com.projects.complaintManagementSystem.entity.User;
import com.projects.complaintManagementSystem.repository.UserRepository;
import io.jsonwebtoken.Claims;
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

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
            log.debug("Authorization header: {}", request.getHeader("Authorization"));

            String path = request.getServletPath();

            if (path.equals("/auth/")) {
                filterChain.doFilter(request, response);
                return;
            }

            /// not block sign up
            final String requestHeader = request.getHeader("Authorization");

            if (requestHeader == null || !requestHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = requestHeader.substring(7);

            Claims claims = authUtil.extractClaims(token);
            Long userId = Long.parseLong(claims.getSubject());

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

//                Optional: block inactive users
//                if (!user.isActive() || user.getAccountStatusType() != AccountStatusType.ACTIVE) {
//                    throw new RuntimeException("Account is not active");
//                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);

                // if i sign in and request for addresses then the backend is unable to get user
//                authToken.setDetails( // block is exactly the reason
//                        new WebAuthenticationDetailsSource().buildDetails(request));
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
