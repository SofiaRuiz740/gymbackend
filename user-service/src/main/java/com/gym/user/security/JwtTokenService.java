package com.gym.user.security;

import com.gym.user.config.JwtProperties;
import com.gym.user.domain.UserAccount;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenService {

    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(UserAccount userAccount) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.expirationMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(userAccount.getId().toString())
                .issuer(jwtProperties.issuer())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .claim("username", userAccount.getUsername())
                .claim("roles", List.of(userAccount.getRole().name()))
                .signWith(signingKey())
                .compact();
    }

    public Instant calculateExpiration() {
        return Instant.now().plus(jwtProperties.expirationMinutes(), ChronoUnit.MINUTES);
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }
}

