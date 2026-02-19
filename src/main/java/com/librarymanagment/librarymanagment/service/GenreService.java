package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.GenreDto;
import com.librarymanagment.librarymanagment.entity.Genre;
import com.librarymanagment.librarymanagment.exception.GenreException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenreService {
    GenreDto createGenre(GenreDto genredto);
    List<GenreDto> getAllGenre();
    GenreDto getGenreById(Long id) throws GenreException;
    GenreDto updateGenre(Long id, GenreDto genreDto) throws GenreException;
    void deleteGenre(Long id) throws GenreException;
    void hardDeleteGenre(Long genreId) throws GenreException;
    List<GenreDto> getAllActiveGenresWithSubGenres();
    List<GenreDto> getTopLevelGenres();
    Page<GenreDto> searchGenres(String searchterm, Pageable pageable);
    long getTotalActiveGenres();
    long getBookCountByGenre(Long genreId);

}
