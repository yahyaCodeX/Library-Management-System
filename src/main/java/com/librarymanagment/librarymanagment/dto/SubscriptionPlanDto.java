package com.librarymanagment.librarymanagment.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPlanDto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Plan code is required")
    private String planCode;

    @NotBlank(message = "name  is required")
    private String name;

    private String description;

    @NotNull(message = "Duration in days is required")
    @Positive(message = "Duration must be a positive integer")
    private Integer durationDays;

    @NotNull(message = " price is required")
    @Positive(message = "Price must be a positive number")
    private Long price;

    private String currency;

    @NotNull(message = "Max Books Allowed is required")
    @Positive(message = "Max books allowed must be a positive integer")
    private Integer maxBooksAllowed;

    @NotNull(message = "max days per book is required")
    @Positive(message = "Max days per book must be a positive integer")
    private Integer maxDaysPerBook;


    private Integer displayOrder;

    private Boolean isActive;
    private Boolean isFeatured;
    private String badgeText;
    private String adminNotes;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
}
