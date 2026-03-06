 package com.librarymanagment.librarymanagment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelSubscriptionRequest {

    @NotBlank(message = "Cancellation reason is required")
    private String cancellationReason;
}

