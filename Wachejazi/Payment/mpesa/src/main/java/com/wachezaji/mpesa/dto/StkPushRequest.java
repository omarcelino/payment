package com.wachezaji.mpesa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record StkPushRequest(
    @NotNull UUID paymentId,
    @NotBlank String msisdn,
    @NotNull @Positive Integer amount,
    @NotBlank String orderReference) {
}
