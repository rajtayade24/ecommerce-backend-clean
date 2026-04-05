package com.projects.complaintManagementSystem.security;

import com.projects.complaintManagementSystem.entity.User;
import com.projects.complaintManagementSystem.enums.AuthProviderType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthUtil {

    @Value("${jwt.secretkey}")
    private String jwtSecretKey;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        String subject = String.valueOf(user.getId());

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", user.getRoles().stream()
                        .map(Enum::name)
                        .toList())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getUserIdFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (Exception ex) {
            throw new RuntimeException("Invalid/expired JWT token", ex);
        }
    }

    public AuthProviderType getProviderTypeFromRegistrationId(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> AuthProviderType.GOOGLE;
            case "twitter" -> AuthProviderType.TWITTER;
            case "facebook" -> AuthProviderType.FACEBOOK;
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
        };
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            throw new RuntimeException("Invalid/expired JWT token", ex);
        }
    }
//    public boolean isTokenValid(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getSecretKey())
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (Exception ex) {
//            return false;
//        }
//    }

    // public String determineProviderIdFromOAuth2User(OAuth2User oAuth2User, String
    // registrationId) {
    // String providerId = switch (registrationId.toLowerCase()) {
    // case "google" -> oAuth2User.getAttribute("sub");
    // default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: "
    // + registrationId);
    // };

    // if (providerId == null || providerId.isBlank()) {
    // throw new IllegalArgumentException("Unable to detarmine provider for OAuth2
    // login");
    // }
    // return providerId;
    // }

    // public String determineUsernameFromOAuth2User(OAuth2User oAuth2User, String
    // registrationId, String providerId) {
    // String email = oAuth2User.getAttribute("email");
    // if (email != null && !email.isBlank()) return email;

    // return switch (registrationId.toLowerCase()) {
    // case "google" -> oAuth2User.getAttribute("sub");
    // default -> providerId;
    // };
    // }
}
