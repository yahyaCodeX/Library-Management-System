package com.librarymanagment.librarymanagment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequest {
    @NotNull(message = "bookId cannot be null")
    private Long bookId;

    private String notes;
}
