package com.librarymanagment.librarymanagment.dto;

import com.librarymanagment.librarymanagment.constant.FineStatus;
import com.librarymanagment.librarymanagment.constant.FineType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FineDto {
    private long id;

    @NotNull(message = "Book loan ID is required")
    private long bookLoanId;

    private String bookTitle;

    private String bookIsbn;

    @NotNull(message = "userId is required")
    private Long userId;

    private String userName;

    private String userEmail;

    @NotNull(message = "Fine type is required")
    private FineType fineType;

    @NotNull(message = "Fine amount is required")
    @PositiveOrZero(message = "Fine amount must be zero or positive")
    private Long amount;

    @PositiveOrZero(message = "Amount paid must be zero or positive")
    private Long amountPaid;

    private Long amountOutstanding;

    @NotNull(message = "Fine status is required")
    private FineStatus fineStatus;

    private String reason;

    private String notes;

    private Long waivedByUserId;

    private String waivedByUserName;

    private LocalDateTime waivedAt;

    private String waivedReason;

    private LocalDateTime paidAt;

    private Long processedByUserId;

    private String processedByUserName;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;





}
