package com.librarymanagment.librarymanagment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionPlanDto {

    private Long id;

    @NotBlank(message = "Plan code is required")
    private String planCode;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Duration in days is required")
    @Positive(message = "Duration must be a positive integer")
    private Integer durationDays;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    private Long price;

    private String currency;

    @NotNull(message = "Max books allowed is required")
    @Positive(message = "Max books allowed must be a positive integer")
    private Integer maxBooksAllowed;

    @NotNull(message = "Max days per book is required")
    @Positive(message = "Max days per book must be a positive integer")
    private Integer maxDaysPerBook;

    private Integer displayOrder;

    private Boolean isActive;
    private Boolean isFeatured;
    private String badgeText;
    private String adminNotes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
}
