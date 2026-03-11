package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.PaymentDto;
import com.librarymanagment.librarymanagment.dto.request.PaymentInitiateRequest;
import com.librarymanagment.librarymanagment.dto.request.PaymentVerifyRequest;
import com.librarymanagment.librarymanagment.dto.response.PaymentInitiateResponse;
import com.librarymanagment.librarymanagment.service.Implementations.PaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// ─── What does this class do? ────────────────────────────────────────────────
// This is the ENTRY POINT for all payment HTTP requests.
// It receives requests from Postman or your frontend and passes them to PaymentServiceImpl.
//
// Endpoints:
//   POST /api/payments/initiate          → user starts a payment
//   POST /api/payments/verify            → manual check (optional)
//   POST /api/payments/webhook/stripe    → Stripe calls this after payment (NO JWT needed)
//   GET  /api/payments/all               → Admin gets all payments
// ─────────────────────────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    // ─── 1. Initiate Payment ──────────────────────────────────────────────────
    // User calls this to start a payment.
    // Returns a checkoutUrl — frontend redirects user to that URL.

    @PostMapping("/initiate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @Valid @RequestBody PaymentInitiateRequest request) {

        PaymentInitiateResponse response = paymentService.initiatePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─── 2. Stripe Webhook ────────────────────────────────────────────────────
    // ⚠️ THIS IS THE MOST IMPORTANT ENDPOINT
    // Stripe calls this URL automatically after the user pays.
    // NO JWT token — Stripe doesn't have your users' tokens!
    //
    // WHY HttpServletRequest instead of @RequestBody String?
    // Stripe signs the EXACT bytes it sends. If Spring's @RequestBody alters even
    // one character (encoding, whitespace, BOM), the HMAC signature won't match.
    // Reading raw bytes from request.getInputStream() gives us the EXACT bytes.
    //
    // We always return 200 — if we return 400/500, Stripe retries 100+ times.

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> stripeWebhook(HttpServletRequest request) {

        try {
            // Read the EXACT raw bytes Stripe sent — no modification by Spring
            String rawPayload = new String(request.getInputStream().readAllBytes());
            String stripeSignature = request.getHeader("Stripe-Signature");

            paymentService.handleWebhook(rawPayload, stripeSignature);
        } catch (Exception e) {
            System.err.println("⚠️ Webhook processing error: " + e.getMessage());
        }
        return ResponseEntity.ok("Webhook received");
    }

    // ─── 3. Verify Payment (manual fallback) ──────────────────────────────────
    // Optional — only needed if you want to manually check payment status.
    // Normally the webhook handles this automatically.

    @PostMapping("/verify")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentDto> verifyPayment(
            @Valid @RequestBody PaymentVerifyRequest request) {

        PaymentDto paymentDto = paymentService.verifyPayment(request);
        return ResponseEntity.ok(paymentDto);
    }

    // ─── 4. Get All Payments (Admin only) ─────────────────────────────────────

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentDto>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(paymentService.getAllPayments(pageable));
    }
}

