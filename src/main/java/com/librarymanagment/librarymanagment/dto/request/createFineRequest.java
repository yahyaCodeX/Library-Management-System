package com.librarymanagment.librarymanagment.dto.request;

import com.librarymanagment.librarymanagment.constant.FineType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class createFineRequest {

    @NotNull(message = "Book loan ID is required")
    private Long bookLoanId;

    @NotNull(message = "Fine type is required")
    private FineType fineType;

    @NotNull(message = "Fine amount is required")
    @PositiveOrZero(message = "Fine amount must be zero or positive")
    private Long amount;

    private String reason;

    private String notes;
}
