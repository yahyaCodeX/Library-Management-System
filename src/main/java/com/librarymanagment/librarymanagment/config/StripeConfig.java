package com.librarymanagment.librarymanagment.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// ─── What does this class do? ────────────────────────────────────────────────
// Stripe SDK works with a GLOBAL static API key.
// This means you set the key ONCE when the app starts, and every Stripe call
// in the whole app automatically uses that key.
// @PostConstruct means: "run this method right after Spring creates this bean"
// ─────────────────────────────────────────────────────────────────────────────

@Configuration
public class StripeConfig {

    // Spring reads this value from application.properties → stripe.secret.key
    @Value("${stripe.secret.key}")
    private String secretKey;

    // This runs automatically when the Spring app starts
    @PostConstruct
    public void initStripe() {
        // This ONE line sets the Stripe secret key globally for the entire app
        // After this, every Stripe.create(), Session.create() etc. uses this key
        Stripe.apiKey = secretKey;
    }
}

