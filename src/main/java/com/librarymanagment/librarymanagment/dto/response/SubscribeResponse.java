package com.librarymanagment.librarymanagment.dto.response;

import com.librarymanagment.librarymanagment.dto.SubscriptionDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscribeResponse {

    private SubscriptionDto subscription;

    // Payment info — user must pay before subscription becomes active
    private Long paymentId;
    private String transactionId;
    private String checkoutUrl;    // ← redirect user here to pay on Stripe

    private String message;
    private Boolean success;
}

