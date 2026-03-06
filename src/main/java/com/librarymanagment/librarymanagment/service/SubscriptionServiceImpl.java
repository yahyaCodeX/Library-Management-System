package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.SubscriptionDto;
import com.librarymanagment.librarymanagment.entity.Subscription;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.exception.SubscriptionException;
import com.librarymanagment.librarymanagment.mapper.SubscriptionMapper;
import com.librarymanagment.librarymanagment.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserServiceImpl userService;

    // ─── Subscribe ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public SubscriptionDto subscribe(SubscriptionDto dto) {
        // Resolve current authenticated user from JWT token
        User currentUser = userService.getCurrentUser();

        // Check if user already has an active subscription
        subscriptionRepository
                .findActiveSubscriptionByUserId(currentUser.getId(), LocalDate.now())
                .ifPresent(existing -> {
                    throw new SubscriptionException(
                            "User already has an active subscription (id: " + existing.getId()
                            + "). Cancel it before subscribing to a new plan.");
                });

        // Force userId from security context — never trust the request body
        dto.setUserId(currentUser.getId());

        // toEntity() resolves User + Plan from DB, copies snapshot, computes endDate
        Subscription subscription = subscriptionMapper.toEntity(dto);


        Subscription saved = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDto(saved);
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
