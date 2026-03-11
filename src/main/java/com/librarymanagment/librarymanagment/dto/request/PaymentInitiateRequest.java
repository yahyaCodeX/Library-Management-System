package com.librarymanagment.librarymanagment.dto.request;

import com.librarymanagment.librarymanagment.constant.PaymentGateway;
import com.librarymanagment.librarymanagment.constant.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiateRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long bookLoanId;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;


    @NotNull(message = "Payment gateway is required")
    private PaymentGateway gateway;

    // Amount in smallest currency unit (e.g., paisa for PKR, paise for INR)
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Long amount;

    // Currency code — "inr", "usd", "eur" etc. Defaults to "inr"
    private String currency = "inr";

    @Size(max = 500,message = "Description can be at most 500 characters")
    private String description;

    private Long fineId;

    private Long subscriptionId;

    // URL to redirect after successful payment
    @Size(max=500,message = "Success URL can be at most 500 characters")
    private String successUrl;

    // URL to redirect after failed/cancelled payment
    @Size(max=500,message = "Cancel URL can be at most 500 characters")
    private String cancelUrl;
}

