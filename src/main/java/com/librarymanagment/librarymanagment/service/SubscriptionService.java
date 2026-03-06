package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.SubscriptionDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto subscribe(SubscriptionDto dto);
    SubscriptionDto getUsersActiveSubscription(Long userId);
    SubscriptionDto cancelSubscription(Long subscriptionId, String cancellationReason);
    SubscriptionDto activeSubscription(Long  subscriptionId,Long paymentId);
    List<SubscriptionDto> getAllSubscriptions(Pageable pageable);
    void deactivateExpiredSubscriptions();
}
