package com.librarymanagment.librarymanagment.dto.request;

import com.librarymanagment.librarymanagment.constant.PaymentGateway;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVerifyRequest {

    // Internal payment record id created during initiation
    @NotNull(message = "Payment ID is required")
    private Long paymentId;

    @NotNull(message = "Payment gateway is required")
    private PaymentGateway gateway;

    // Gateway-returned payment id (e.g. Razorpay pay_xxx / Stripe pi_xxx)
    @NotBlank(message = "Gateway payment ID is required")
    private String gatewayPaymentId;

    // Order / session id returned by gateway at checkout
    @NotBlank(message = "Gateway order ID is required")
    private String gatewayOrderId;

    // HMAC signature returned by gateway (used for Razorpay verification)
    private String gatewaySignature;
}
