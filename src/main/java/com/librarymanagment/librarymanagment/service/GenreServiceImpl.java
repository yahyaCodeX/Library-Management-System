package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.GenreDto;
import com.librarymanagment.librarymanagment.entity.Genre;
import com.librarymanagment.librarymanagment.exception.GenreException;
import com.librarymanagment.librarymanagment.mapper.GenreMapper;
import com.librarymanagment.librarymanagment.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;


    @Override
    public GenreDto createGenre(GenreDto genreDto) {
        // 3. Map DTO to Entity
        Genre genre = genreMapper.toEntity(genreDto);
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toDTO(savedGenre);
    }

    @Override
    public List<GenreDto> getAllGenre() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GenreDto getGenreById(Long id) throws GenreException {
        Genre genre=genreRepository.findById(id)
                .orElseThrow(()->new GenreException("genre not found"));
        return genreMapper.toDTO(genre);
    }

    @Override
    public GenreDto updateGenre(Long id, GenreDto genreDto) {
        return null;
    }

    @Override
    public void deleteGenre(Long id) {

    }

    @Override
    public void hardDeleteGenre(Long genreId) {

    }

    @Override
    public List<GenreDto> getAllActiveGenresWithSubGenres() {
        return List.of();
    }

    @Override
    public List<GenreDto> getTopLevelGenres() {
        return List.of();
    }

    @Override
    public Page<GenreDto> searchGenres(String searchterm, Pageable pageable) {
        return null;
    }

    @Override
    public long getTotalActiveGenres() {
        return 0;
    }

    @Override
    public long getBookCountByGenre(Long genreId) {
        return 0;
    }
}
