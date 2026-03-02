package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.ApiResponse;
import com.librarymanagment.librarymanagment.dto.SubscriptionPlanDto;
import com.librarymanagment.librarymanagment.service.SubscriptionPlanService;
import com.librarymanagment.librarymanagment.service.SubscriptionPlanServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanServiceImpl subscriptionPlanService;

    @PostMapping("/admin/create")
    public ResponseEntity<SubscriptionPlanDto> createSubscriptionPlan(
            @Valid @RequestBody SubscriptionPlanDto planDto) {
        SubscriptionPlanDto created = subscriptionPlanService.createSubscriptionPlan(planDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<SubscriptionPlanDto> updateSubscriptionPlan(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionPlanDto planDto) {
        SubscriptionPlanDto updated = subscriptionPlanService.updateSubscriptionPlan(id, planDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<ApiResponse> deleteSubscriptionPlan(@PathVariable Long id) {
        subscriptionPlanService.deleteSubscriptionPlan(id);
        return ResponseEntity.ok(new ApiResponse("Subscription plan with id " + id + " deleted successfully", true));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionPlanDto>> getAllSubscriptionPlans() {
        List<SubscriptionPlanDto> plans = subscriptionPlanService.getAllSubscriptionPlans();
        return ResponseEntity.ok(plans);
    }
}
