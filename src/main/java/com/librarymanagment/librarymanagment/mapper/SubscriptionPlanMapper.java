package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.SubscriptionPlanDto;
import com.librarymanagment.librarymanagment.entity.SubscriptionPlan;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SubscriptionPlanMapper {

    public SubscriptionPlanDto toDto(SubscriptionPlan subscriptionPlan) {
        if (subscriptionPlan == null) {
            return null;
        }

        return SubscriptionPlanDto.builder()
                .id(subscriptionPlan.getId())
                .planCode(subscriptionPlan.getPlanCode())
                .name(subscriptionPlan.getName())
                .description(subscriptionPlan.getDescription())
                .durationDays(subscriptionPlan.getDurationDays())
                .price(subscriptionPlan.getPrice())
                .currency(subscriptionPlan.getCurrency())
                .maxBooksAllowed(subscriptionPlan.getMaxBooksAllowed())
                .maxDaysPerBook(subscriptionPlan.getMaxDaysPerBook())
                .displayOrder(subscriptionPlan.getDisplayOrder())
                .isActive(subscriptionPlan.getIsActive())
                .isFeatured(subscriptionPlan.getIsFeatured())
                .badgeText(subscriptionPlan.getBadgeText())
                .adminNotes(subscriptionPlan.getAdminNotes())
                .createdAt(subscriptionPlan.getCreatedAt())
                .updatedAt(subscriptionPlan.getUpdatedAt())
                .createdBy(subscriptionPlan.getCreatedBy())
                .updatedBy(subscriptionPlan.getUpdatedBy())
                .build();
    }

    public SubscriptionPlan toEntity(SubscriptionPlanDto subscriptionPlanDto) {
        if (subscriptionPlanDto == null) {
            return null;
        }

        return SubscriptionPlan.builder()
                .id(subscriptionPlanDto.getId())
                .planCode(subscriptionPlanDto.getPlanCode())
                .name(subscriptionPlanDto.getName())
                .description(subscriptionPlanDto.getDescription())
                .durationDays(subscriptionPlanDto.getDurationDays())
                .price(subscriptionPlanDto.getPrice())
                .currency(subscriptionPlanDto.getCurrency() != null ? subscriptionPlanDto.getCurrency() : "PKR")
                .maxBooksAllowed(subscriptionPlanDto.getMaxBooksAllowed())
                .maxDaysPerBook(subscriptionPlanDto.getMaxDaysPerBook())
                .displayOrder(subscriptionPlanDto.getDisplayOrder() != null ? subscriptionPlanDto.getDisplayOrder() : 0)
                .isActive(subscriptionPlanDto.getIsActive() != null ? subscriptionPlanDto.getIsActive() : true)
                .isFeatured(subscriptionPlanDto.getIsFeatured() != null ? subscriptionPlanDto.getIsFeatured() : false)
                .badgeText(subscriptionPlanDto.getBadgeText())
                .adminNotes(subscriptionPlanDto.getAdminNotes())
                .createdBy(subscriptionPlanDto.getCreatedBy())
                .updatedBy(subscriptionPlanDto.getUpdatedBy())
                .build();
    }

    public void updateEntityFromDto(SubscriptionPlanDto dto, SubscriptionPlan entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setPlanCode(dto.getPlanCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDurationDays(dto.getDurationDays());
        entity.setPrice(dto.getPrice());
        entity.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "PKR");
        entity.setMaxBooksAllowed(dto.getMaxBooksAllowed());
        entity.setMaxDaysPerBook(dto.getMaxDaysPerBook());
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        entity.setIsFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);
        entity.setBadgeText(dto.getBadgeText());
        entity.setAdminNotes(dto.getAdminNotes());
        entity.setUpdatedBy(dto.getUpdatedBy());
    }

    public List<SubscriptionPlanDto> toDtoList(List<SubscriptionPlan> subscriptionPlans) {
        if (subscriptionPlans == null || subscriptionPlans.isEmpty()) {
            return List.of();
        }

        return subscriptionPlans.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
