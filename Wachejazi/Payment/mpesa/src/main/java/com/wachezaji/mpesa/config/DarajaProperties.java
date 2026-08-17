package com.wachezaji.mpesa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mpesa")
@Getter
@Setter
public class DarajaProperties {

    private Daraja daraja = new Daraja();
    private String callbackUrl;

    @Getter
    @Setter
    public static class Daraja {
        private String baseUrl;
        private String consumerKey;
        private String consumerSecret;
        private String passkey;
        private String shortcode;
    }
}