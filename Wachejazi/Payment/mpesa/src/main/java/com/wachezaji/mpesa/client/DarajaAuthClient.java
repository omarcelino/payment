package com.wachezaji.mpesa.client;

import com.wachezaji.mpesa.config.DarajaProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;

@Component
public class DarajaAuthClient {

    private final RestClient restClient;
    private final DarajaProperties.Daraja daraja;

    public DarajaAuthClient(DarajaProperties darajaProperties) {
        this.daraja = darajaProperties.getDaraja();
        this.restClient = RestClient.create(daraja.getBaseUrl());
    }

    public String getAccessToken() {
        String credentials = daraja.getConsumerKey() + ":" + daraja.getConsumerSecret();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        Map<String, Object> response = restClient.get()
                .uri("/oauth/v1/generate?grant_type=client_credentials")
                .header("Authorization", "Basic " + encodedCredentials)
                .retrieve()
                .body(Map.class);

        return (String) response.get("access_token");
    }
}