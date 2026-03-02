package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.SubscriptionPlanDto;
import com.librarymanagment.librarymanagment.entity.SubscriptionPlan;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.exception.SubscriptionPlanException;
import com.librarymanagment.librarymanagment.mapper.SubscriptionPlanMapper;
import com.librarymanagment.librarymanagment.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final UserServiceImpl userServiceImpl;

    @Override
    public SubscriptionPlanDto createSubscriptionPlan(SubscriptionPlanDto planDto) {
        // Check for duplicate plan code
        if (subscriptionPlanRepository.existsByPlanCode(planDto.getPlanCode())) {
            throw new SubscriptionPlanException("Subscription plan with code '" + planDto.getPlanCode() + "' already exists");
        }

        SubscriptionPlan plan = subscriptionPlanMapper.toEntity(planDto);
        User currentUser=userServiceImpl.getCurrentUser();
        plan.setCreatedBy(currentUser.getFullName());
        plan.setUpdatedBy(currentUser.getFullName());
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);

        return subscriptionPlanMapper.toDto(savedPlan);
    }

    @Override
    public SubscriptionPlanDto updateSubscriptionPlan(Long planId, SubscriptionPlanDto planDto) {
        SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new SubscriptionPlanException("Subscription plan not found with id: " + planId));

        // If plan code is being changed, check for duplicates
        if (!existingPlan.getPlanCode().equals(planDto.getPlanCode()) &&
                subscriptionPlanRepository.existsByPlanCode(planDto.getPlanCode())) {
            throw new SubscriptionPlanException("Subscription plan with code '" + planDto.getPlanCode() + "' already exists");
        }

        subscriptionPlanMapper.updateEntityFromDto(planDto, existingPlan);
        User currentUser=userServiceImpl.getCurrentUser();
        existingPlan.setCreatedBy(currentUser.getFullName());
        existingPlan.setUpdatedBy(currentUser.getFullName());
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(existingPlan);
        return subscriptionPlanMapper.toDto(updatedPlan);
    }

    @Override
    public void deleteSubscriptionPlan(Long planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new SubscriptionPlanException("Subscription plan not found with id: " + planId));

        subscriptionPlanRepository.delete(plan);
    }

    @Override
    public List<SubscriptionPlanDto> getAllSubscriptionPlans() {
        List<SubscriptionPlan> plans = subscriptionPlanRepository.findAll();
        return subscriptionPlanMapper.toDtoList(plans);
    }
}
