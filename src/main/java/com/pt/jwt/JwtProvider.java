package com.pt.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@Slf4j
public class JwtProvider {
    @Value("${jwt.security.access-token-key}")
    private String atSecurityKey;
    @Value("${jwt.access-token.expired.time}")
    private long atExpiredTime;
    @Value("${jwt.security.refresh-token-key}")
    private String rtSecurityKey;
    @Value("${jwt.refresh-token.expired.time}")
    private long rtExpiredTime;

    public String generateAtToken(Authentication authentication) {
        return generateToken(authentication, atSecurityKey, atExpiredTime);
    }

    public String generateRtToken(Authentication authentication) {
        return generateToken(authentication, rtSecurityKey, rtExpiredTime);
    }

    public String generateAtToken(String email) {
        return generateToken(email, atSecurityKey, atExpiredTime);
    }

    public String generateRtToken(String email) {
        return generateToken(email, rtSecurityKey, rtExpiredTime);
    }

    public String generateToken(Authentication authen, String securityKey, long expiration) {
        Date tokenCreateTime = new Date();
        Date tokenExpiredTime = new Date(new Date().getTime() + expiration);
        return Jwts.builder()
                .setSubject(authen.getName()).claim("role", authen.getAuthorities())
                .setIssuedAt(tokenCreateTime)
                .setExpiration(tokenExpiredTime)
                .signWith(SignatureAlgorithm.HS256, securityKey)
                .compact();
    }

    public String generateToken(String email, String securityKey, long expiration) {
        Date tokenCreateTime = new Date();
        Date tokenExpiredTime = new Date(new Date().getTime() + expiration);
        return Jwts.builder()
                .setSubject(email).claim("role", "USER")
                .setIssuedAt(tokenCreateTime)
                .setExpiration(tokenExpiredTime)
                .signWith(SignatureAlgorithm.HS256, securityKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(atSecurityKey).parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, atSecurityKey);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, rtSecurityKey);
    }

    public boolean validateToken(String token, String securityKey) {
        try {
            Jwts.parser().setSigningKey(securityKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
            throw new SignatureException("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
            throw new MalformedJwtException("Invalid JWT token");

        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
            throw e;
        }
    }

    public String getTokenFromRequest(HttpServletRequest httpServletRequest) {
        final String bearerToken = httpServletRequest.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getTokenFromRequest(HttpHeaders headers) {
        String bearerToken = headers.get("Authorization").get(0);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
