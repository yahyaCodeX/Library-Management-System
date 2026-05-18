package com.librarymanagment.librarymanagment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.IMessage;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookReviewDto {
    private Long id;

    @NotNull(message = "User Id is mandatory")
    private Long userId;

    private String userName;

    @NotNull(message = "Book Id is mandatory")
    private Long bookId;

    private String bookTitle;

    @NotNull(message = "Rating is mandatory")
    @Min(value = 1,message = "rating must be atleast 1")
    @Max(value = 5,message = "Rating must not exceed 5")
    private Integer rating;

    @NotBlank(message = "Review Test is mandatory")
    @Size(min = 10,max = 2000,message = "Review must be between 10 to 2000 words")
    private String reviewText;

    @Size(max = 200, message = "Review title must not exceed 200 chars")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;



}
