package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.ApiResponse;
import com.librarymanagment.librarymanagment.dto.GenreDto;
import com.librarymanagment.librarymanagment.exception.GenreException;
import com.librarymanagment.librarymanagment.service.GenreServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreServiceImpl genreService;

    @PostMapping("/create")
    public ResponseEntity<GenreDto> addGenre(@RequestBody GenreDto genre){
        GenreDto createdgenre=genreService.createGenre(genre);
        return ResponseEntity.ok(createdgenre);
    }

    @GetMapping()
    public ResponseEntity<List<GenreDto>> getAllGenre(){
        List<GenreDto> genres=genreService.getAllGenre();
        return ResponseEntity.ok(genres);
    }
    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Long genreId) throws GenreException {
        GenreDto genreById = genreService.getGenreById(genreId);
        return ResponseEntity.ok(genreById);
    }

    @PutMapping("/{genreId}")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable Long genreId, @RequestBody GenreDto genre) throws GenreException {
        GenreDto updatedGenre = genreService.updateGenre(genreId, genre);
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<ApiResponse> deleteGenre(@PathVariable Long genreId) throws GenreException {
        genreService.deleteGenre(genreId);
        return ResponseEntity.ok(new ApiResponse("Genre deleted Soft-Delete",true));
    }
    @DeleteMapping("/{genreId}/hard")
    public ResponseEntity<ApiResponse> hardDeleteGenre(@PathVariable Long genreId) throws GenreException {
        genreService.hardDeleteGenre(genreId);
        return ResponseEntity.ok(new ApiResponse("Genre deleted permanently",true));
    }

    @GetMapping("/top-level-genres")
    public ResponseEntity<List<GenreDto>> getTopLevelGenres(){
        List<GenreDto> topLevelGenres = genreService.getTopLevelGenres();
        return ResponseEntity.ok(topLevelGenres);
    }
    @GetMapping("/active-genres")
    public ResponseEntity<Long> getActivegenresCount(){
        long totalActiveGenres = genreService.getTotalActiveGenres();
        return ResponseEntity.ok(totalActiveGenres);
    }
    @GetMapping("/{genreId}/book-count")
    public ResponseEntity<Long> getBookCountByGenre(@PathVariable Long genreId) throws GenreException {
        long bookCount = genreService.getBookCountByGenre(genreId);
        return ResponseEntity.ok(bookCount);
    }



}
