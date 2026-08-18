package com.wachezaji.mpesa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StkCorrelation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String checkoutRequestId;

    @Column(nullable = false)
    private UUID paymentId;

    @Column(nullable = false)
    private Instant requestedAt;

    @Column(nullable = false)
    private boolean resolved = false;

    public StkCorrelation(String checkoutRequestId, UUID paymentId) {
        this.checkoutRequestId = checkoutRequestId;
        this.paymentId = paymentId;
        this.requestedAt = Instant.now();
    }
}
