package com.librarymanagment.librarymanagment.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookReviewRequest {
    @NotNull(message = "Book ID is mandatory")
    private Long bookId;


    @NotNull(message = "Rating is mandatory")
    @Min(value = 1,message = "Rating must be at least 1")
    @Max(value=5,message = "Rating must not exceed 5")
    private Integer rating;


    @NotNull(message = "Review text is mandatory")
    @Size(min = 10,max = 2000,message = "Review must be between 10 to 2000 words")
    private String reviewText;


    @Size(max = 200,message = "Review ttile must not exceed 200 chars")
    private String reviewTitle;
}
