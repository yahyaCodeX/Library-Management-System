package com.librarymanagment.librarymanagment.repository;

import com.librarymanagment.librarymanagment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Used in webhook handler to find our DB record using Stripe's session ID
    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);
}
