package com.example.backend.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

public class KeycloakConfig {

    private static String serverUrl = "http://localhost:8080";
    public static String realm = "master";
    private static String userName = "gianluigi_03";
    private static String password = "password";

    private static String clientId = "my-app-client";
    private static String clientSecret = "bpCT8d0eRc6OVoqTq5GHDSkKbrnJBk2Q";
    private static Keycloak keycloak = null;

    private KeycloakConfig() {

    }
    public static Keycloak getInstance(){
        if(keycloak == null){

            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(userName)
                    .password(password)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();

        }
        return keycloak;
    }
}
