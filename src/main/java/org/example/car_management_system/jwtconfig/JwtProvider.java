package org.example.car_management_system.jwtconfig;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserDetailsService userDetailsService;

    // Generate accessToken
    public String generateToken(Authentication auth) {
        return JWT.create()
                .withExpiresAt(Instant.now().plusMillis(jwtProperties.getKeyExpiresAt()))
                .withSubject(auth.getName())
                .withClaim("role", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .findFirst().orElse("ROLE_USER"))
                .sign(Algorithm.HMAC512(jwtProperties.getPrivateKey()));
    }
    // validate token
    public boolean validateAccessToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(jwtProperties.getPrivateKey()))
                    .acceptExpiresAt(jwtProperties.getKeyExpiresAt())
                    .withClaimPresence("sub")
                    .withClaimPresence("role")
                    .build()
                    .verify(token);

            String email = jwt.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (!userDetails.isEnabled()) {
                return false;
            }

            String tokenRole = jwt.getClaim("role").asString();
            if (tokenRole == null) {
                return false;
            }

            return userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals(tokenRole));
        } catch (Exception e) {
            return false;
        }
    }

    public String generateRefreshToken(Authentication auth) {
        return JWT.create()
                .withExpiresAt(Instant.now().plusMillis(jwtProperties.getRefreshExpiresAt()))
                .withIssuedAt(Instant.now())
                .withSubject(auth.getName())
                .sign(Algorithm.HMAC512(jwtProperties.getRefreshKey()));
    }

    public boolean validateRefreshToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(jwtProperties.getRefreshKey()))
                    .acceptExpiresAt(jwtProperties.getRefreshExpiresAt())
                    .withClaimPresence("sub")
                    .withClaimPresence("iat")
                    .build()
                    .verify(token);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication createAuthentication(String token) {
        DecodedJWT jwt = JWT.decode(token);
        String username = jwt.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities()
        );
    }

    public Date getExpiryDateFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt(); // Lấy ngày hết hạn từ token
    }
}
