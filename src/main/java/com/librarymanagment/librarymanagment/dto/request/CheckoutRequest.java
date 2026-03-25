package com.librarymanagment.librarymanagment.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {

    // Optional - can be provided via URL path parameter or in request body
    // For /checkout endpoint: provide in body
    // For /checkout/user/{userId} endpoint: provide in URL path
    private Long userId;

    @NotNull(message = "Book ID is required")
    private Long bookId;

    // Optional; defaults to current date in service if omitted.
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkoutDate;

    // Optional; service can compute from lending policy when null.
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Size(max = 500, message = "Notes can be at most 500 characters")
    private String notes;
}
