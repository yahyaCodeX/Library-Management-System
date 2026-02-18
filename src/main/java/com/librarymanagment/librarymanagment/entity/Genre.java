package com.librarymanagment.librarymanagment.entity;

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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Genre code is required")
    private String code;
    @NotBlank(message = "Genre name is required")
    private String name;

    @Size(max = 500,message = "Description cannot exceed 500 characters")
    private String description;

    @Min(value = 0,message = "Display order must be a non-negative integer")
    private Integer displayOrder=0;

    @Column(nullable = false)
    private Boolean active=true;

    @ManyToOne
    private Genre parentGenre;

    @OneToMany(mappedBy = "parentGenre")
    private List<Genre> subGenres=new ArrayList<Genre>();

//    @OneToMany(mappedBy = "genre",cascade = CascadeType.PERSIST)
//    private List<Book> books=new ArrayList<Book>();

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;





}
