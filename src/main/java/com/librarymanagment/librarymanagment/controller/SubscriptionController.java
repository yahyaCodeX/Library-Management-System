package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.ApiResponse;
import com.librarymanagment.librarymanagment.dto.SubscriptionDto;
import com.librarymanagment.librarymanagment.dto.request.CancelSubscriptionRequest;
import com.librarymanagment.librarymanagment.service.SubscriptionServiceImpl;
import com.librarymanagment.librarymanagment.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionServiceImpl subscriptionService;
    private final UserServiceImpl userService;

    // ─── Subscribe (User) ────────────────────────────────────────────────────

    @PostMapping("/subscribe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDto> subscribe(@Valid @RequestBody SubscriptionDto dto) {
        SubscriptionDto result = subscriptionService.subscribe(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ─── Get My Active Subscription (User) ───────────────────────────────────

    @GetMapping("/active/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDto> getMyActiveSubscription() {
        // userId resolved from security context inside the service
        SubscriptionDto result = subscriptionService.getUsersActiveSubscription(
                userService.getCurrentUser().getId());
        return ResponseEntity.ok(result);
    }

    // ─── Get Active Subscription by userId (Admin) ───────────────────────────

    @GetMapping("/active/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionDto> getActiveSubscriptionByUserId(
            @PathVariable Long userId) {
        SubscriptionDto result = subscriptionService.getUsersActiveSubscription(userId);
        return ResponseEntity.ok(result);
    }

    // ─── Cancel Subscription ─────────────────────────────────────────────────

    @PatchMapping("/{subscriptionId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDto> cancelSubscription(
            @PathVariable Long subscriptionId,
            @Valid @RequestBody CancelSubscriptionRequest request) {
        SubscriptionDto result = subscriptionService.cancelSubscription(
                subscriptionId, request.getCancellationReason());
        return ResponseEntity.ok(result);
    }

    // ─── Activate Subscription (Admin / Payment callback) ────────────────────

    @PatchMapping("/{subscriptionId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionDto> activateSubscription(
            @PathVariable Long subscriptionId,
            @RequestParam Long paymentId) {
        SubscriptionDto result = subscriptionService.activeSubscription(subscriptionId, paymentId);
        return ResponseEntity.ok(result);
    }

    // ─── Deactivate Expired Subscriptions (Admin / Scheduler) ────────────────

    @PatchMapping("/deactivate-expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deactivateExpired() {
        subscriptionService.deactivateExpiredSubscriptions();
        return ResponseEntity.ok(
                new ApiResponse("Expired subscriptions deactivated ", true));
    }

    // ─── Get All Subscriptions (Admin) ───────────────────────────────────────

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionDto>> getAllSubscriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        List<SubscriptionDto> result = subscriptionService.getAllSubscriptions(pageable);
        return ResponseEntity.ok(result);
    }
}
