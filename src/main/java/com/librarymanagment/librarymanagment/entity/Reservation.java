package com.librarymanagment.librarymanagment.entity;

import com.librarymanagment.librarymanagment.constant.ReservationStatus;
import jakarta.persistence.*;
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
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Book book;

    private ReservationStatus reservationStatus=ReservationStatus.PENDING;

    private LocalDateTime reservedAt;
    private LocalDateTime availableAt;
    private LocalDateTime availableUntil;
    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false,nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public boolean canBeCancelled(){
        return reservationStatus==ReservationStatus.PENDING
                || reservationStatus==ReservationStatus.AVAILABLE;
    }

    public boolean hasExpired(){
        return reservationStatus==ReservationStatus.AVAILABLE
                && availableUntil!=null
                && LocalDateTime.now().isAfter(availableUntil);
    }
}
