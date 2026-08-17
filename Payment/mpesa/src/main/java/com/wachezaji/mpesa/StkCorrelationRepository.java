package com.wachezaji.mpesa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StkCorrelationRepository extends JpaRepository<StkCorrelation, UUID> {

    Optional<StkCorrelation> findByCheckoutRequestId(String checkoutRequestId);
}
