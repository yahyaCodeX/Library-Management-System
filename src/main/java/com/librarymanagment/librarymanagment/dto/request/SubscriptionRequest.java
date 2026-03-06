package com.librarymanagment.librarymanagment.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionRequest {

    @NotNull(message = "Subscription plan ID is required")
    private Long subscriptionPlanId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;   // optional – defaults to today if null

    private Boolean autoReview;

    private String notes;
}

