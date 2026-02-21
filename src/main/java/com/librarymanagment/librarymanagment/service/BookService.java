package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.BookDTO;
import com.librarymanagment.librarymanagment.dto.request.BookSearchRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.exception.BookException;

import java.util.List;

public interface BookService {
    BookDTO createBook(BookDTO bookDTO) throws BookException;
    List<BookDTO> createBooksInBulk(List<BookDTO> bookDTOList) throws BookException;
    BookDTO getBookById(Long id) throws BookException;
    BookDTO getBookByISBN(String isbn) throws BookException;
    BookDTO updateBook(Long id, BookDTO bookDTO) throws BookException;
    void deleteBook(Long id) throws BookException;
    void hardDeleteBook(Long id) throws BookException;

    PageResponse<BookDTO> searchBooksWithFilters(
            BookSearchRequest searchRequest
    );

    long getTotalActiveBooks();
    long getTotalAvailableBooks();
}
