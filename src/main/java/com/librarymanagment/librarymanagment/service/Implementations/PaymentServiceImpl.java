package com.librarymanagment.librarymanagment.service.Implementations;

import com.librarymanagment.librarymanagment.constant.PaymentGateway;
import com.librarymanagment.librarymanagment.constant.PaymentStatus;
import com.librarymanagment.librarymanagment.constant.PaymentType;
import com.librarymanagment.librarymanagment.dto.PaymentDto;
import com.librarymanagment.librarymanagment.dto.request.PaymentInitiateRequest;
import com.librarymanagment.librarymanagment.dto.request.PaymentVerifyRequest;
import com.librarymanagment.librarymanagment.dto.response.PaymentInitiateResponse;
import com.librarymanagment.librarymanagment.entity.Payment;
import com.librarymanagment.librarymanagment.entity.Subscription;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.exception.PaymentException;
import com.librarymanagment.librarymanagment.mapper.PaymentMapper;
import com.librarymanagment.librarymanagment.repository.PaymentRepository;
import com.librarymanagment.librarymanagment.repository.SubscriptionRepository;
import com.librarymanagment.librarymanagment.repository.UserRespository;
import com.librarymanagment.librarymanagment.service.Gateway.StripeService;
import com.librarymanagment.librarymanagment.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

// ─── What does this class do? ────────────────────────────────────────────────
// This is the BRAIN of the payment module.
// It coordinates between:
//   1. Your DATABASE (PaymentRepository, SubscriptionRepository, UserRepository)
//   2. STRIPE (via StripeService)
//
// Flow:
//   initiatePayment()
//     → calls StripeService.createCheckoutSession()
//     → saves a PENDING payment record in DB
//     → returns checkoutUrl to the controller
//
//   handleWebhook()
//     → called when Stripe sends payment confirmation
//     → verifies signature via StripeService
//     → updates payment status in DB to SUCCESS or FAILED
// ─────────────────────────────────────────────────────────────────────────────

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRespository userRespository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final SubscriptionServiceImpl subscriptionService;

    @Override
    @Transactional
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) {

        User user = userRespository.findById(request.getUserId())
                .orElseThrow(() -> new PaymentException("User not found with id: " + request.getUserId()));

        Subscription subscription = null;
        if (request.getSubscriptionId() != null) {
            subscription = subscriptionRepository.findById(request.getSubscriptionId())
                    .orElseThrow(() -> new PaymentException("Subscription not found with id: " + request.getSubscriptionId()));
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 16);

        String currency = (request.getCurrency() != null) ? request.getCurrency().toLowerCase() : "USD";

        String productName = (request.getDescription() != null)
                ? request.getDescription()
                : "Library Subscription";

        Session stripeSession = stripeService.createCheckoutSession(
                request.getAmount(),   // amount in smallest unit (paise for INR, cents for USD)
                currency,
                productName,
                transactionId          // stored inside Stripe metadata for reference
        );

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setSubscription(subscription);
        payment.setPaymentType(request.getPaymentType() != null ? request.getPaymentType() : PaymentType.MEMBERSHIP);
        payment.setPaymentStatus(PaymentStatus.PENDING);  // ← PENDING until webhook confirms
        payment.setGateway(PaymentGateway.STRIPE);
        payment.setAmount(request.getAmount());
        payment.setTransactionId(transactionId);
        payment.setGatewayOrderId(stripeSession.getId()); // Stripe session ID stored here
        payment.setDescription(request.getDescription());

        Payment saved = paymentRepository.save(payment);

        return PaymentInitiateResponse.builder()
                .paymentId(saved.getId())
                .gateway(PaymentGateway.STRIPE)
                .transactionId(transactionId)
                .stripeSessionId(stripeSession.getId())    // e.g. "cs_test_XXXX"
                .amount(request.getAmount())
                .currency(currency)
                .description(request.getDescription())
                .checkoutUrl(stripeSession.getUrl())       // ← redirect user here
                .message("Payment session created. Redirect user to checkoutUrl.")
                .success(true)
                .build();
    }


    @Transactional
    public void handleWebhook(String rawPayload, String signatureHeader) {
        Event event = stripeService.verifyWebhookSignature(rawPayload, signatureHeader);

        String eventType = event.getType();

        if (!eventType.equals("checkout.session.completed") && !eventType.equals("checkout.session.expired")) {
            System.out.println("ℹ️ Ignoring event type: " + eventType);
            return;
        }

        // ── STEP 2: Extract session ID from the raw JSON payload ──────────────
        String sessionId;
        String paymentIntentId;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawPayload);
            JsonNode dataObject = root.path("data").path("object");
            sessionId = dataObject.path("id").asText(null);
            paymentIntentId = dataObject.path("payment_intent").asText(null);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to parse webhook JSON: " + e.getMessage());
            return;
        }

        if (sessionId == null || sessionId.isBlank()) {
            System.err.println("⚠️ Session ID is null in webhook payload, skipping");
            return;
        }

        System.out.println("🔍 Session ID from webhook: " + sessionId);

        // ── STEP 3: Find our payment record in DB ─────────────────────────────
        Payment payment = paymentRepository.findByGatewayOrderId(sessionId).orElse(null);

        if (payment == null) {
            System.err.println("⚠️ No payment record found for session: " + sessionId + ", skipping");
            return;
        }

        // ── STEP 4: Update payment status based on event type ─────────────────
        if (eventType.equals("checkout.session.completed")) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setGatewayPaymentId(paymentIntentId);
            payment.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            System.out.println("✅ Payment marked SUCCESS for session: " + sessionId);

            // ── STEP 5: Auto-activate the linked subscription ─────────────────
            if (payment.getSubscription() != null) {
                subscriptionService.activateSubscriptionAfterPayment(
                        payment.getSubscription().getId());
                System.out.println("✅ Subscription " + payment.getSubscription().getId()
                        + " activated via payment webhook");
            }
        } else {
            // checkout.session.expired
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Stripe checkout session expired");
            paymentRepository.save(payment);
            System.out.println("❌ Payment marked FAILED (expired) for session: " + sessionId);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public PaymentDto verifyPayment(PaymentVerifyRequest request) {

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new PaymentException("Payment not found with id: " + request.getPaymentId()));

        return  paymentMapper.toDto(payment);
    }

    // ─── 4. Get All Payments (Admin) ──────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toDto);
    }
}
