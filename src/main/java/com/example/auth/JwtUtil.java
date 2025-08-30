package com.example.auth;

import com.example.util.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "gbfddbhghrethy485tgergbvdjfbgjeyty34t5yreughfdbjgdfthy4835398wtghdsfgsfls";
    private static final String AUTHORITIES = "authorities";


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractName(String token) {
        return extractClaim(token, claims -> claims.get(Constants.NAME, String.class));
    }

    public List<? extends GrantedAuthority> extractRoles(String token) {
        List<?> rawRoleList = extractClaim(token, claims -> claims.get(AUTHORITIES, List.class));
        // Cast each element to GrantedAuthority and collect to a new list
        return rawRoleList.stream()
                .map(String.class::cast)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        //return extractClaim(token, (Claims claims) -> claims.get(AUTHORITIES, List.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private Claims extractAllClaims(String token) {
        return (Claims) Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parse(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    public CustomUserPrincipal buildUserPrincipal(String jwt, List<? extends GrantedAuthority> authorities) {
        return new CustomUserPrincipal(extractUsername(jwt), extractName(jwt), authorities);
    }
}
