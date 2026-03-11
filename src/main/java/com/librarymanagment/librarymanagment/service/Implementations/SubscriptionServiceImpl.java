package com.librarymanagment.librarymanagment.service.Implementations;

import com.librarymanagment.librarymanagment.constant.PaymentGateway;
import com.librarymanagment.librarymanagment.constant.PaymentStatus;
import com.librarymanagment.librarymanagment.constant.PaymentType;
import com.librarymanagment.librarymanagment.dto.SubscriptionDto;
import com.librarymanagment.librarymanagment.dto.response.SubscribeResponse;
import com.librarymanagment.librarymanagment.entity.Payment;
import com.librarymanagment.librarymanagment.entity.Subscription;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.exception.SubscriptionException;
import com.librarymanagment.librarymanagment.mapper.SubscriptionMapper;
import com.librarymanagment.librarymanagment.repository.PaymentRepository;
import com.librarymanagment.librarymanagment.repository.SubscriptionRepository;
import com.librarymanagment.librarymanagment.service.Gateway.StripeService;
import com.librarymanagment.librarymanagment.service.SubscriptionService;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserServiceImpl userService;
    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;

    // ─── Subscribe (creates INACTIVE subscription + Stripe payment) ──────────

    @Override
    @Transactional
    public SubscribeResponse subscribe(SubscriptionDto dto) {
        // 1. Resolve current authenticated user from JWT token
        User currentUser = userService.getCurrentUser();

        // 2. Check if user already has an active subscription
        subscriptionRepository
                .findActiveSubscriptionByUserId(currentUser.getId(), LocalDate.now())
                .ifPresent(existing -> {
                    throw new SubscriptionException(
                            "User already has an active subscription (id: " + existing.getId()
                            + "). Cancel it before subscribing to a new plan.");
                });

        // 3. Force userId from security context — never trust the request body
        dto.setUserId(currentUser.getId());

        // 4. toEntity() resolves User + Plan from DB, copies snapshot, computes endDate
        Subscription subscription = subscriptionMapper.toEntity(dto);

        // 5. ⚠️ KEY CHANGE: Set subscription as INACTIVE until payment succeeds
        subscription.setIsActive(false);

        Subscription saved = subscriptionRepository.save(subscription);

        // 6. Create Stripe checkout session for this subscription's price
        String transactionId = "TXN-" + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 16);
        String currency = saved.getSubscriptionPlan().getCurrency() != null
                ? saved.getSubscriptionPlan().getCurrency().toLowerCase()
                : "usd";
        String productName = saved.getPlanName() + " - " + saved.getSubscriptionPlan().getDurationDays() + " days";

        Session stripeSession = stripeService.createCheckoutSession(
                saved.getPrice(),      // amount from the plan
                currency,
                productName,
                transactionId
        );

        // 7. Save a PENDING payment record linked to this subscription
        Payment payment = new Payment();
        payment.setUser(currentUser);
        payment.setSubscription(saved);
        payment.setPaymentType(PaymentType.MEMBERSHIP);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setGateway(PaymentGateway.STRIPE);
        payment.setAmount(saved.getPrice());
        payment.setTransactionId(transactionId);
        payment.setGatewayOrderId(stripeSession.getId());
        payment.setDescription("Subscription: " + productName);

        Payment savedPayment = paymentRepository.save(payment);

        // 8. Return response with subscription info + checkout URL
        return SubscribeResponse.builder()
                .subscription(subscriptionMapper.toDto(saved))
                .paymentId(savedPayment.getId())
                .transactionId(transactionId)
                .checkoutUrl(stripeSession.getUrl())
                .message("Subscription created. Please complete payment to activate it. Redirect to checkoutUrl.")
                .success(true)
                .build();
    }

    // ─── Get Active Subscription ─────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public SubscriptionDto getUsersActiveSubscription(Long userId) {
        Subscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDate.now())
                .orElseThrow(() -> new SubscriptionException(
                        "No active subscription found for user id: " + userId));

        return subscriptionMapper.toDto(subscription);
    }

    // ─── Cancel Subscription ─────────────────────────────────────────────────

    @Override
    @Transactional
    public SubscriptionDto cancelSubscription(Long subscriptionId, String cancellationReason) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException(
                        "Subscription not found with id: " + subscriptionId));

        if (!subscription.getIsActive()) {
            throw new SubscriptionException(
                    "Subscription with id: " + subscriptionId + " is already cancelled.");
        }

        subscription.setIsActive(false);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancellationReason(cancellationReason!=null ? cancellationReason : "Cancelled by user");

        Subscription saved = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDto(saved);
    }

    // ─── Activate Subscription ───────────────────────────────────────────────

    @Override
    @Transactional
    public SubscriptionDto activeSubscription(Long subscriptionId, Long paymentId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException(
                        "Subscription not found with id: " + subscriptionId));

        if (subscription.getIsActive()) {
            throw new SubscriptionException(
                    "Subscription with id: " + subscriptionId + " is already active.");
        }

        subscription.setIsActive(true);
        subscription.setCancelledAt(null);
        subscription.setCancellationReason(null);

        // Re-calculate dates from today when reactivating
        subscription.setStartDate(LocalDate.now());
        subscription.initializeFromPlan();

        Subscription saved = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDto(saved);
    }

    // ─── Activate Subscription After Successful Payment (called by webhook) ─

    @Override
    @Transactional
    public void activateSubscriptionAfterPayment(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException(
                        "Subscription not found with id: " + subscriptionId));

        if (subscription.getIsActive()) {
            System.out.println("ℹ️ Subscription " + subscriptionId + " is already active, skipping activation");
            return;
        }

        // Activate the subscription and start dates from today (payment day)
        subscription.setIsActive(true);
        subscription.setStartDate(LocalDate.now());
        subscription.initializeFromPlan();
        subscription.setCancelledAt(null);
        subscription.setCancellationReason(null);

        subscriptionRepository.save(subscription);
        System.out.println("✅ Subscription " + subscriptionId + " activated after successful payment");
    }

    // ─── Get All Subscriptions (Admin) ───────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDto> getAllSubscriptions(Pageable pageable) {
        return subscriptionRepository.findAll(pageable)
                .stream()
                .map(subscriptionMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deactivateExpiredSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredActiveSubscriptions(LocalDate.now());
        expiredSubscriptions.forEach(subscription -> subscription.setIsActive(false));
        subscriptionRepository.saveAll(expiredSubscriptions);
    }
}
