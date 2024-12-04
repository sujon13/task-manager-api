package com.example.config;

import com.example.auth.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String PREFIX = "/api/v1";
    private final JwtRequestFilter jwtRequestFilter;
    private final AccessDeniedHandler accessDeniedHandler;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            PREFIX + "/likes",
            PREFIX + "/questions",
            PREFIX + "/questions/*",
            PREFIX + "/exams",
            PREFIX + "/exams/*/questions",
            "/error"
    );

    public static List<String> getPublicEndpoints() {
        return PUBLIC_ENDPOINTS;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS.toArray(new String[0])).permitAll()

                        .requestMatchers(HttpMethod.POST, PREFIX + "/likes").hasAnyRole("USER")
                        //.requestMatchers(HttpMethod.GET, PREFIX + "/likes").permitAll()
                        .requestMatchers(PREFIX + "/comments", PREFIX + "/comments/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.POST, PREFIX + "/questions").hasAnyRole("QUESTIONER")
                        //.requestMatchers(HttpMethod.GET, PREFIX + "/questions", PREFIX + "/questions/*").permitAll()
                        .requestMatchers(HttpMethod.PATCH, PREFIX + "/questions/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.PUT, PREFIX + "/questions/**").hasAnyRole("QUESTIONER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, PREFIX + "/topics").hasAnyRole("QUESTIONER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, PREFIX + "/topics/*").hasRole( "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, PREFIX + "/topics/*").hasRole( "ADMIN")

                        .requestMatchers(HttpMethod.POST, PREFIX + "/exams").hasAnyRole("EXAMINER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, PREFIX + "/exams/*/clone")
                            .hasAnyRole("EXAMINER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, PREFIX + "/exams/*/submissions").hasRole("USER")
                        //.requestMatchers(HttpMethod.GET, PREFIX + "/exams").permitAll()
                        .requestMatchers(HttpMethod.POST, PREFIX + "/exams/*/enter", PREFIX + "/exams/*/exit").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, PREFIX + "/exams/*").hasAnyRole("EXAMINER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, PREFIX + "/exams/*/cancel", PREFIX + "/exams/*/reschedule")
                            .hasAnyRole("EXAMINER", "ADMIN", "USER")

                        .requestMatchers(HttpMethod.POST, PREFIX + "/exams/*/questions").hasAnyRole("EXAMINER", "ADMIN")
                        //.requestMatchers(HttpMethod.GET, PREFIX + "/exams/*/questions").permitAll()
                        .requestMatchers(HttpMethod.PUT, PREFIX + "/exams/*/questions/*").hasAnyRole("EXAMINER", "ADMIN")

                        .requestMatchers(PREFIX + "/posts", PREFIX + "/posts/*").hasAnyRole("EXAMINER", "ADMIN")
                        .requestMatchers(PREFIX + "/exam-takers", PREFIX + "/exam-takers/*").hasAnyRole("EXAMINER", "ADMIN")

                        .anyRequest().authenticated()  // Secure other endpoints
                )
                .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedMethod(CorsConfiguration.ALL);
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addExposedHeader("Location"); // Expose the Location header
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
