package com.main.icrsbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ for Postman + React requests
                .csrf(csrf -> csrf.disable())

                // ✅ allow iframe preview (React 5173 -> backend 8080)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // ✅ IMPORTANT: enable cors and connect it to CorsConfigurationSource bean below
                .cors(Customizer.withDefaults())

                // ✅ allow all for now (development)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().permitAll()
                )

                // ✅ disable default login methods
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // ✅ This is the missing piece: Spring Security needs this for CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        // ✅ if you use cookies/auth later you can keep true,
        // but then you must NOT use "*" in allowedOrigins.
        config.setAllowCredentials(true);

        // ✅ cache preflight response
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // apply to all endpoints (api + uploads)
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
