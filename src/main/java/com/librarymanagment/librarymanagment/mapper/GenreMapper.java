package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.GenreDto;
import com.librarymanagment.librarymanagment.entity.Genre;
import com.librarymanagment.librarymanagment.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
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
                    .map(this::toDTO)
                    .collect(Collectors.toList())
            );
        }

        return responseDto;
    }

    private String generateCode(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "GENRE_" + System.currentTimeMillis();
        }
        return name.trim().toUpperCase()
                .replaceAll("[^A-Z0-9\\s]", "")
                .replaceAll("\\s+", "_");
    }

    public Genre toEntity(GenreDto genreDto){
        if(genreDto==null){
            return null;
        }
        String code = genreDto.getCode();
        if (code == null || code.trim().isEmpty()) {
            code = generateCode(genreDto.getName());
        } else {
            code = code.trim().toUpperCase().replaceAll("\\s+", "_");
        }

        Genre genre = Genre.builder()
                .code(code)
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

    public void updateEntityFromDto(GenreDto genreDto, Genre genre){
        if(genreDto==null || genre==null){
            return;
        }
        String code = genreDto.getCode();
        if (code == null || code.trim().isEmpty()) {
            code = generateCode(genreDto.getName());
        } else {
            code = code.trim().toUpperCase().replaceAll("\\s+", "_");
        }
        genre.setCode(code);
        genre.setName(genreDto.getName());
        genre.setDescription(genreDto.getDescription());
        genre.setDisplayOrder(genreDto.getDisplayOrder()!=null ? genreDto.getDisplayOrder():0);
        genre.setActive(genreDto.getActive());

        // Handle Parent Genre safely
        if (genreDto.getParentGenreId() != null) {
            Genre parent = genreRepository.findById(genreDto.getParentGenreId())
                    .orElseThrow(() -> new RuntimeException("Parent Genre not found"));
            genre.setParentGenre(parent);
        }
    }

    public List<GenreDto> toDTOList(List<Genre> genres){
        if(genres==null || genres.isEmpty()){
            return List.of();
        }
        return genres.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}