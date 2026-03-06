package com.librarymanagment.librarymanagment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionDto {


    private Long id;

    // User info
    private Long userId;
    private String userEmail;
    private String userName;

    // Plan info
    private Long subscriptionPlanId;
    private String planName;
    private String planCode;
    private Long price;
    private String currency;

    // Limits
    private Integer maxBooksAllowed;
    private Integer maxDaysPerBook;


    // Dates
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    // Status
    private Boolean isActive;
    private Boolean autoRenew;

    // Cancellation
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    // Extra
    private String notes;

    // Computed fields
    private Boolean isValid;
    private Boolean isExpired;
    private Long remainingDays;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
