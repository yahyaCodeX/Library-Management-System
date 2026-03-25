package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.BookLoanDto;
import com.librarymanagment.librarymanagment.entity.BookLoan;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class BookLoanMapper {
    public BookLoanDto mapToDto(BookLoan bookLoan) {
        if (bookLoan == null) {
            return null;
        }

        return BookLoanDto.builder()
                .id(bookLoan.getId())
                .userId(bookLoan.getUser().getId())
                .userName(bookLoan.getUser().getFullName())
                .userEmail(bookLoan.getUser().getEmail())
                .bookId(bookLoan.getBook().getId())
                .bookTitle(bookLoan.getBook().getTitle())
                .bookIsbn(bookLoan.getBook().getIsbn())
                .bookAuthor(bookLoan.getBook().getAuthor())
                .bookCOverImage(bookLoan.getBook().getCoverImageUrl())
                .loanType(bookLoan.getLoanType())
                .status(bookLoan.getStatus())
                .checkoutDate(bookLoan.getCheckoutDate())
                .dueDate(bookLoan.getReturnDate())
                .remainingDays((long)calculateRemainingDays(bookLoan.getReturnDate()))
                .returnDate(bookLoan.getReturnDate())
                .renewalCount(bookLoan.getRenewalCount())
                .maxRenewals(bookLoan.getMaxRenewals())
                .notes(bookLoan.getNotes())
                .isOverdue(bookLoan.getIsOverdue())
                .overdueDays(bookLoan.getOverdueDays())
                .createdAt(bookLoan.getCreatedAt())
                .updatedAt(bookLoan.getUpdatedAt())
                .build();
    }
    public int calculateRemainingDays(LocalDate dueDate) {
        LocalDate today = LocalDate.now();
        if (today.isAfter(dueDate)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(today, dueDate);
    }
}
