package com.librarymanagment.librarymanagment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.librarymanagment.librarymanagment.constant.BookLoanStatus;
import com.librarymanagment.librarymanagment.constant.BookLoanType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookLoanDto {
    private Long id;

    @NotNull(message = "User id is required")
    private Long userId;
    private String userName;
    private String userEmail;

    @NotNull(message = "Book id is required")
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private String bookAuthor;
    private String bookCOverImage;

    private BookLoanType loanType;
    private BookLoanStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkoutDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private Long remainingDays;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;

    private Integer renewalCount;
    private Integer maxRenewals;
    private BigDecimal fineAmount;
    private Boolean isFinePaid;
    private String notes;
    private Boolean isOverdue;
    private Integer overdueDays;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
