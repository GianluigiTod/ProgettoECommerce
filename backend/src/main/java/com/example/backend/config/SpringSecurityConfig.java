package com.example.backend.config;


import com.example.backend.config.JWT.JwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final JwtAuthenticationConverter jwtAuthConverter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {
                    CorsConfigurationSource source = request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:8280"));
                        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setAllowCredentials(true);
                        return config;
                    };
                    cors.configurationSource(source);
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(antMatcher("/api/user/register")).permitAll()
                        .requestMatchers(antMatcher("/api/images/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .sessionManagement(sess -> sess.sessionCreationPolicy(STATELESS));
        return http.build();
    }


    /*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors-> {
                    CorsConfigurationSource source = request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
                        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setAllowCredentials(true);
                        return config;
                    };
                    cors.configurationSource(source);
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(antMatcher("/api/user/register")).permitAll()
                        .requestMatchers(antMatcher("/api/images/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .sessionManagement(sess -> sess.sessionCreationPolicy(STATELESS));
        return http.build();
    }





    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin(null);
        configuration.addAllowedHeader("*");
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedOrigin("http://localhost:8280");
        configuration.addAllowedHeader("Access-Control-Allow-Headers");
        configuration.addAllowedHeader("Access-Control-Allow-Origin");
        configuration.addAllowedHeader("Access-Control-Allow-Methods");
        configuration.addExposedHeader("Authorization");
        configuration.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }

     */



}
