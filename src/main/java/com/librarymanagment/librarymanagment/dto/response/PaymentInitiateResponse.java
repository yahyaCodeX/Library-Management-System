package com.librarymanagment.librarymanagment.dto.response;

import com.librarymanagment.librarymanagment.constant.PaymentGateway;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiateResponse {
    private Long paymentId;
    private PaymentGateway gateway;
    private String transactionId;

    // Stripe Checkout Session ID — frontend does NOT need this directly
    // Just redirect the user to checkoutUrl
    private String stripeSessionId;

    private Long amount;
    private String currency;
    private String description;

    // ✅ THE MOST IMPORTANT FIELD
    // Redirect the user to this URL — Stripe hosts the payment page
    private String checkoutUrl;

    private String message;
    private Boolean success;
}
