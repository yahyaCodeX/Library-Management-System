package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.GenreDto;
import com.librarymanagment.librarymanagment.entity.Genre;
import com.librarymanagment.librarymanagment.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreMapper {
    private final GenreRepository genreRepository;

    public  GenreDto toDTO(Genre savedGenre){
        if(savedGenre == null){
            return null;
        }
        // 4. Map saved Entity back to DTO
        GenreDto responseDto = GenreDto.builder()
                .id(savedGenre.getId()) // Now ID will not be null
                .code(savedGenre.getCode())
                .name(savedGenre.getName())
                .description(savedGenre.getDescription())
                .displayOrder(savedGenre.getDisplayOrder())
                .active(savedGenre.getActive())
                .createdAt(savedGenre.getCreatedAt())
                .updatedAt(savedGenre.getUpdatedAt())
                .build();

        if (savedGenre.getParentGenre() != null) {
            responseDto.setParentGenreId(savedGenre.getParentGenre().getId());
            responseDto.setParentGenreName(savedGenre.getParentGenre().getName());
        }

        if(savedGenre.getSubGenres()!=null && !savedGenre.getSubGenres().isEmpty()){
            responseDto.setSubGenre(savedGenre.getSubGenres().stream()
                    .filter(Genre::getActive)
                    .map(GenreMapper::toDTO)
                    .collect(Collectors.toList())
            );
        }

        return responseDto;
    }

    public Genre toEntity(GenreDto genreDto){
        if(genreDto==null){
            return null;
        }
        Genre genre = Genre.builder()
                .code(genreDto.getCode())
                .name(genreDto.getName())
                .description(genreDto.getDescription())
                .displayOrder(genreDto.getDisplayOrder())
                .active(true)
                .build();

        // 2. Handle Parent Genre safely
        if (genreDto.getParentGenreId() != null) {
            Genre parent = genreRepository.findById(genreDto.getParentGenreId())
                    .orElseThrow(() -> new RuntimeException("Parent Genre not found"));
            genre.setParentGenre(parent);
        }
        return genre;
    }
}
