package com.librarymanagment.librarymanagment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "subscriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SubscriptionPlan subscriptionPlan;

    private String planName;

    private String planCode;
    private Long price;

    @Column(nullable = false)
    private Integer maxBooksAllowed;

    @Column(nullable = false)
    private Integer maxDaysPerBook;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Boolean isActive=true;

    private Boolean autoRenew;

    private LocalDateTime cancelledAt;

    private String cancellationReason;

    private String notes;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean isValid(){
        if(!isActive){
            return false;
        }
        LocalDate today=LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    public long getRemainingDays() {
        if (isExpired()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }
    public void calculateEndDate(){
        if(subscriptionPlan!=null && startDate!=null){
            this.endDate=this.startDate.plusDays(subscriptionPlan.getDurationDays());
        }
    }

    public void initializeFromPlan(){
        if(subscriptionPlan!=null){
            this.planName=subscriptionPlan.getName();
            this.planCode=subscriptionPlan.getPlanCode();
            this.price=subscriptionPlan.getPrice();
            this.maxBooksAllowed=subscriptionPlan.getMaxBooksAllowed();
            this.maxDaysPerBook=subscriptionPlan.getMaxDaysPerBook();
            if(startDate==null){
                this.startDate=LocalDate.now();
            }
            calculateEndDate();
        }
    }




}
