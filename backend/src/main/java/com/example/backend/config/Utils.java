package com.example.backend.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

public class Utils {
    static Logger logger = LoggerFactory.getLogger(Utils.class);
    public static String getUser() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (authenticationToken == null) {
            logger.error("Authentication token is null");
            return null;
        }

        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        if (jwt == null) {
            logger.error("JWT is null");
            return null;
        }

        Map<String, Object> claims = jwt.getClaims();
        logger.info("JWT Claims: " + claims); // Logga tutti i claims

        String username = (String) claims.get("preferred_username");
        if (username == null) {
            logger.error("Username claim is missing");
        }
        logger.info(username);
        return username;
    }


}
