package com.librarymanagment.librarymanagment.dto;

import com.librarymanagment.librarymanagment.constant.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDto {
    private Long id;

    //User details
    private Long userId;
    private String userName;
    private String userEmail;

    //Book details
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private String bookAuthor;
    private Boolean isBookAvailable;

    //Reservation details
    private ReservationStatus reservationStatus;
    private LocalDateTime reservedAt;
    private LocalDateTime availableAt;
    private LocalDateTime availableUntil;
    private LocalDateTime fulfilledAt;
    private LocalDateTime cancelledAt;
    private Integer queuePosition;
    private Boolean notificationSent;
    private String notes;

    //Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Computed fields
    private boolean isExpired;
    private boolean canBeCancelled;
    private Long hoursUntilExpiry; //hours remaining for pickup


}
