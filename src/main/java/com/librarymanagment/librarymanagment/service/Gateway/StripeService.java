package com.librarymanagment.librarymanagment.service.Gateway;

import com.librarymanagment.librarymanagment.exception.PaymentException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class StripeService {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public Session createCheckoutSession(
            Long amountInCents,  // e.g. $5.00 = 500 cents  OR  ₹500 = 50000 paise
            String currency,     // "usd", "inr", "eur" etc.
            String productName,  // shown on Stripe payment page e.g. "Basic Plan - 30 days"
            String transactionId // your internal ID — stored in Stripe for reference
    ) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)

                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(currency)       // "inr" or "usd"
                                                    .setUnitAmount(amountInCents) // smallest unit
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(productName)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )

                    .setSuccessUrl(frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(frontendUrl + "/payment/cancel")
                    .putMetadata("transactionId", transactionId)

                    .build();
            return Session.create(params);

        } catch (StripeException e) {
            throw new PaymentException("Failed to create Stripe checkout session: " + e.getMessage());
        }
    }


    public Event verifyWebhookSignature(String rawPayload, String stripeSignatureHeader) {
        try {
            return Webhook.constructEvent(rawPayload, stripeSignatureHeader, webhookSecret);

        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid Stripe webhook signature: " + e.getMessage());
        } catch (Exception e) {
            throw new PaymentException("Failed to parse Stripe webhook: " + e.getMessage());
        }
    }
}

