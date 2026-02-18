package com.librarymanagment.librarymanagment.repository;

import com.librarymanagment.librarymanagment.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre,Long> {
    List<Genre> findByActiveTrueOrderByDisplayOrderAsc();
    List<Genre> findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();
    List<Genre> findByParentGenreIdAndActiveTrueOrderByDisplayOrderAsc(Long parentGenreId);
    long countByActiveTrue();
//    @Query("SELECT COUNT(b) FROM Book b WHERE b.genre.id = :genreId")
//    long countBooksByGenre(@Param("genreId")Long genreId);
}
