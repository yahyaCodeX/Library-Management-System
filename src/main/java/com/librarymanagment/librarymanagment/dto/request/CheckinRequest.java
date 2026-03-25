package com.librarymanagment.librarymanagment.dto.request;

import com.librarymanagment.librarymanagment.constant.BookLoanStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckinRequest {

    @NotNull(message = "Book loan ID is required")
    private Long bookLoanId;

    private BookLoanStatus condition=BookLoanStatus.RETURNED;

    private String notes;
}
