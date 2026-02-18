package com.librarymanagment.librarymanagment.dto;

import com.librarymanagment.librarymanagment.entity.Genre;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenreDto {


    private Long id;

    @NotBlank(message = "Genre code is required")
    private String code;
    @NotBlank(message = "Genre name is required")
    private String name;

    @Size(max = 500,message = "Description cannot exceed 500 characters")
    private String description;

    @Min(value = 0,message = "Display order must be a non-negative integer")
    private Integer displayOrder=0;


    private Boolean active;

    private Long parentGenreId;

    private String parentGenreName;

    private List<GenreDto> subGenre;

    private Long bookCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
