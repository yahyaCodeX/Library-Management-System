package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.SubscriptionDto;
import com.librarymanagment.librarymanagment.dto.response.SubscribeResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {
    SubscribeResponse subscribe(SubscriptionDto dto);
    SubscriptionDto getUsersActiveSubscription(Long userId);
    SubscriptionDto cancelSubscription(Long subscriptionId, String cancellationReason);
    SubscriptionDto activeSubscription(Long  subscriptionId,Long paymentId);
    void activateSubscriptionAfterPayment(Long subscriptionId);
    List<SubscriptionDto> getAllSubscriptions(Pageable pageable);
    void deactivateExpiredSubscriptions();
}
