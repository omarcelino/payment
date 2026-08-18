package com.wachezaji.mpesa.controller;

import com.wachezaji.mpesa.dto.DarajaCallbackRequest;
import com.wachezaji.mpesa.dto.DarajaStkPushResponse;
import com.wachezaji.mpesa.dto.StkPushRequest;
import com.wachezaji.mpesa.repository.StkCorrelationRepository;
import com.wachezaji.mpesa.service.StkPushService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StkPushController {

    private static final Logger log = LoggerFactory.getLogger(StkPushController.class);

    private final StkPushService stkPushService;
    private final StkCorrelationRepository stkCorrelationRepository;

    public StkPushController(StkPushService stkPushService,
                              StkCorrelationRepository stkCorrelationRepository) {
        this.stkPushService = stkPushService;
        this.stkCorrelationRepository = stkCorrelationRepository;
    }

    @PostMapping("/api/stk-push")
    public DarajaStkPushResponse initiate(@Valid @RequestBody StkPushRequest request) {
        return stkPushService.initiate(request);
    }

    @PostMapping("/api/mpesa/callback")
    public Map<String, Object> handleCallback(@RequestBody DarajaCallbackRequest callbackRequest) {
        var stkCallback = callbackRequest.body().stkCallback();
        String checkoutRequestId = stkCallback.checkoutRequestId();

        var correlation = stkCorrelationRepository.findByCheckoutRequestId(checkoutRequestId);
        if (correlation.isEmpty()) {
            log.warn("Received callback for unknown checkoutRequestId={}", checkoutRequestId);
        } else if (stkCallback.resultCode() == 0) {
            log.info("Payment succeeded for paymentId={} checkoutRequestId={}",
                    correlation.get().getPaymentId(), checkoutRequestId);
        } else {
            log.info("Payment failed for paymentId={} checkoutRequestId={} resultDesc={}",
                    correlation.get().getPaymentId(), checkoutRequestId, stkCallback.resultDesc());
        }

        return Map.of("ResultCode", 0, "ResultDesc", "Accepted");
    }
}
