package com.wachezaji.mpesa.service;

import com.wachezaji.mpesa.client.DarajaAuthClient;
import com.wachezaji.mpesa.config.DarajaProperties;
import com.wachezaji.mpesa.dto.DarajaStkPushResponse;
import com.wachezaji.mpesa.dto.StkPushRequest;
import com.wachezaji.mpesa.entity.StkCorrelation;
import com.wachezaji.mpesa.repository.StkCorrelationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
public class StkPushService {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final RestClient restClient;
    private final DarajaProperties.Daraja daraja;
    private final String callbackUrl;
    private final DarajaAuthClient authClient;
    private final StkCorrelationRepository correlationRepository;

    public StkPushService(DarajaProperties darajaProperties,
                           DarajaAuthClient authClient,
                           StkCorrelationRepository correlationRepository) {
        this.daraja = darajaProperties.getDaraja();
        this.callbackUrl = darajaProperties.getCallbackUrl();
        this.restClient = RestClient.create(daraja.getBaseUrl());
        this.authClient = authClient;
        this.correlationRepository = correlationRepository;
    }

    public DarajaStkPushResponse initiate(StkPushRequest request) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String password = buildPassword(timestamp);

        Map<String, Object> body = Map.ofEntries(
                Map.entry("BusinessShortCode", daraja.getShortcode()),
                Map.entry("Password", password),
                Map.entry("Timestamp", timestamp),
                Map.entry("TransactionType", "CustomerPayBillOnline"),
                Map.entry("Amount", request.amount()),
                Map.entry("PartyA", request.msisdn()),
                Map.entry("PartyB", daraja.getShortcode()),
                Map.entry("PhoneNumber", request.msisdn()),
                Map.entry("CallBackURL", callbackUrl),
                Map.entry("AccountReference", request.orderReference()),
                Map.entry("TransactionDesc", "Payment")
        );

        DarajaStkPushResponse response = restClient.post()
                .uri("/mpesa/stkpush/v1/processrequest")
                .header("Authorization", "Bearer " + authClient.getAccessToken())
                .body(body)
                .retrieve()
                .body(DarajaStkPushResponse.class);

        correlationRepository.save(
                new StkCorrelation(response.checkoutRequestId(), request.paymentId()));

        return response;
    }

    private String buildPassword(String timestamp) {
        String raw = daraja.getShortcode() + daraja.getPasskey() + timestamp;
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }
}
