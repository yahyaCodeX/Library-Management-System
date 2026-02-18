package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.GenreDto;
import com.librarymanagment.librarymanagment.entity.Genre;
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

}
