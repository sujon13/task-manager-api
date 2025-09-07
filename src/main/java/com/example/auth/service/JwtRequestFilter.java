package com.example.auth.service;

import com.example.auth.model.CustomUserPrincipal;
import com.example.config.SecurityConfig;
import com.example.util.Constants;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private String extractJwtTokenFromHeader(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else return null;
    }

    private String extractJwtTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constants.ACCESS_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isPublicEndpoint(final HttpServletRequest request) {
        final String requestPath = request.getRequestURI();
        return SecurityConfig.getPublicEndpoints()
                .stream()
                .anyMatch(pattern -> requestPath.matches(pattern.replace("**", ".*")) &&
                        HttpMethod.GET.matches(request.getMethod())
                );
    }

    private boolean isOptionsRequest(final HttpServletRequest request) {
        return HttpMethod.OPTIONS.matches(request.getMethod());
    }

    private String extractJwtToken(HttpServletRequest request) {
        String jwt = extractJwtTokenFromHeader(request);
        if (jwt == null) {
            log.debug("Token found from the cookie");
            jwt = extractJwtTokenFromCookie(request);
        }
        return jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (isPublicEndpoint(request) || isOptionsRequest(request)) {
            // Skip token validation for public endpoints
            chain.doFilter(request, response);
            return;
        }

        String jwt = extractJwtToken(request);
        if (jwt == null) {
            handleJwtException(response, "JWT Token not found", "JWT Token not found");
            return;
        }

        String username;
        try {
            username = jwtUtil.extractUsername(jwt);
            if (username == null) {
                handleJwtException(response, "Username not found in JWT Token", "Username not found in JWT Token");
                return;
            }
        } catch (ExpiredJwtException e) {
            handleJwtException(response, "JWT Token has expired", e.getMessage());
            return;
        } catch (JwtException e) { // all other JWT errors
            handleJwtException(response, "Invalid JWT", e.getMessage());
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // skip recheck user if it is altered meanwhile
            List<? extends GrantedAuthority> authorities = jwtUtil.extractRoles(jwt);
            final CustomUserPrincipal principal = jwtUtil.buildUserPrincipal(jwt, authorities);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    principal, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }

    private void handleJwtException(HttpServletResponse response, String message, String errorMessage) throws IOException {
        log.error(message);
        log.error(errorMessage);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(message);
    }
}