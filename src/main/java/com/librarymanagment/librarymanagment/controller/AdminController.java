package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.BookDTO;
import com.librarymanagment.librarymanagment.exception.BookException;
import com.librarymanagment.librarymanagment.service.BookServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
public class AdminController {
    private final BookServiceImpl bookServiceImpl;

    @PostMapping("/create")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) throws BookException {
        BookDTO createdBook = bookServiceImpl.createBook(bookDTO);
        return ResponseEntity.ok(createdBook);
    }
}
