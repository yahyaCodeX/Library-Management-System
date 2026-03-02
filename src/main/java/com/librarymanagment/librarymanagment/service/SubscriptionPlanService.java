package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.SubscriptionPlanDto;

import java.util.List;

public interface SubscriptionPlanService {
    SubscriptionPlanDto createSubscriptionPlan(SubscriptionPlanDto planDto);

    SubscriptionPlanDto updateSubscriptionPlan(Long planId,SubscriptionPlanDto planDto);

    void deleteSubscriptionPlan(Long planId);

    List<SubscriptionPlanDto> getAllSubscriptionPlans();
}
