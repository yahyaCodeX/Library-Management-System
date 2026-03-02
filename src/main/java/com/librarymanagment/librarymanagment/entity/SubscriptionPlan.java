package com.librarymanagment.librarymanagment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false,unique = true)
    private String planCode;

    @Column(nullable = false,length = 100)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Long price;

    private String currency="PKR";

    @Column(nullable = false)
    @Positive(message = "Max books allowed must be a positive integer")
    private Integer maxBooksAllowed;

    @Column(nullable = false)
    @Positive(message = "Max days per book must be a positive integer")
    private Integer maxDaysPerBook;

    @Column(nullable = false)
    private Integer displayOrder=0;

    private Boolean isActive=true;
    private Boolean isFeatured=false;

    private String badgeText;

    private String adminNotes;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

}
