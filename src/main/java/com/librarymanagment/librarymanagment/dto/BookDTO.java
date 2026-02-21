package com.librarymanagment.librarymanagment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
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
public class BookDTO {
    private Long id;

    @NotBlank(message = "ISBN is mandatory")
    private String isbn;

    @NotBlank(message = "Title is mandatory")
    @Size(min=1,max = 255,message = "Title must be between 1 and 255 characters")
    private String title;

    @NotBlank(message = "Title is mandatory")
    @Size(min=1,max = 255,message = "author must be between 1 and 255 characters")
    private String author;

    @NotNull(message = "Genre ID is mandatory")
    private Long genreId;

    private String genreName;

    private String genreCode;

    @Size(max = 100,message = "Publisher name must be less than 100 characters")
    private String publisher;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedDate;

    @Size(max = 20,message = "Language must be less than 20 characters")
    private String language;

    @Min(value = 1,message = "Pages must be greater than 0")
    @Max(value = 5000,message = "Pages must be less than 5000")
    private Integer pages;

    @Size(max = 2000,message = "Description must be less than 2000 characters")
    private String description;

    @Min(value = 0,message = "Total copies must be greater than or equal to 0")
    @NotNull(message = "Total copies is mandatory")
    private Integer totalCopies;

    @Min(value = 0,message = "Available copies must be greater than or equal to 0")
    @NotNull(message = "Available copies is mandatory")
    private Integer availableCopies;

    @DecimalMin(value = "0.0",inclusive = true,message = "Price must be greater than 0")
    @Digits(integer = 8,fraction = 2,message = "Price must be a valid decimal number with up to 8 digits and 2 decimal places")
    private BigDecimal price;

    @Size(max=500,message = "Cover image URL must be less than 500 characters")
    private String coverImageUrl;

    private Boolean alreadyHaveLoan;
    private Boolean alreadyHaveReservation;

    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
