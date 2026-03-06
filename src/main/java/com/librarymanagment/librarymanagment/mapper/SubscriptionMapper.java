package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.SubscriptionDto;
import com.librarymanagment.librarymanagment.dto.request.SubscriptionRequest;
import com.librarymanagment.librarymanagment.entity.Subscription;
import com.librarymanagment.librarymanagment.entity.SubscriptionPlan;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.exception.SubscriptionPlanException;
import com.librarymanagment.librarymanagment.repository.SubscriptionPlanRepository;
import com.librarymanagment.librarymanagment.repository.UserRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionMapper {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRespository userRespository;

    // ─── Entity → DTO ────────────────────────────────────────────────────────

    public SubscriptionDto toDto(Subscription subscription) {
        if (subscription == null) {
            return null;
        }

        return SubscriptionDto.builder()
                .id(subscription.getId())
                // user info
                .userId(subscription.getUser().getId())
                .userEmail(subscription.getUser().getEmail())
                .userName(subscription.getUser().getFullName())
                // plan info (snapshot stored on subscription)
                .subscriptionPlanId(subscription.getSubscriptionPlan().getId())
                .planName(subscription.getPlanName())
                .planCode(subscription.getPlanCode())
                .price(subscription.getPrice())
                .currency(subscription.getSubscriptionPlan().getCurrency())
                // limits
                .maxBooksAllowed(subscription.getMaxBooksAllowed())
                .maxDaysPerBook(subscription.getMaxDaysPerBook())
                // dates
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                // status
                .isActive(subscription.getIsActive())
                .autoRenew(subscription.getAutoRenew())
                // cancellation
                .cancelledAt(subscription.getCancelledAt())
                .cancellationReason(subscription.getCancellationReason())
                // extra
                .notes(subscription.getNotes())
                // computed
                .isValid(subscription.isValid())
                .isExpired(subscription.isExpired())
                .remainingDays(subscription.getRemainingDays())
                // audit
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }

    // ─── DTO/Request → Entity ────────────────────────────────────────────────

    /**
     * Creates a new Subscription entity from a SubscriptionRequest.
     * The User must be supplied by the caller (resolved from security context).
     */
    public Subscription toEntity(SubscriptionDto dto) {
        if (dto == null) {
            return null;
        }

        Subscription subscription = new Subscription();

        // Resolve User from DB using userId set by the service
        if (dto.getUserId() != null) {
            User user = userRespository.findById(dto.getUserId())
                    .orElseThrow(() -> new SubscriptionPlanException("User not found with id: " + dto.getUserId()));
            subscription.setUser(user);
        }

        // Resolve Plan from DB using subscriptionPlanId
        if (dto.getSubscriptionPlanId() != null) {
            SubscriptionPlan plan = subscriptionPlanRepository.findById(dto.getSubscriptionPlanId())
                    .orElseThrow(() -> new SubscriptionPlanException("Subscription plan not found with id: " + dto.getSubscriptionPlanId()));
            subscription.setSubscriptionPlan(plan);
        }

        // Set startDate before initializeFromPlan so it is respected
        subscription.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now());

        // Copy plan snapshot fields + compute endDate from plan
        subscription.initializeFromPlan();

        // Optional fields from request
        subscription.setAutoRenew(dto.getAutoRenew() != null && dto.getAutoRenew());
        subscription.setNotes(dto.getNotes());
        subscription.setIsActive(true);

        return subscription;
    }

    public List<SubscriptionDto> toDtoList(List<Subscription> subscriptions) {
        if(subscriptions==null){
            return null;
        }
        return subscriptions.stream().map(this::toDto).toList();
    }
}
