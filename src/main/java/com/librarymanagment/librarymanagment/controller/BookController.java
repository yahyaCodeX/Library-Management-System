package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.ApiResponse;
import com.librarymanagment.librarymanagment.dto.BookDTO;
import com.librarymanagment.librarymanagment.dto.request.BookSearchRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.exception.BookException;
import com.librarymanagment.librarymanagment.service.BookService;
import com.librarymanagment.librarymanagment.service.BookServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
    private final BookServiceImpl bookServiceImpl;



    @PostMapping("/create/bulk")
    public ResponseEntity<List<BookDTO>> createBookInBulk(@Valid @RequestBody List<BookDTO> bookDTOList) throws BookException {
        List<BookDTO> booksInBulk = bookServiceImpl.createBooksInBulk(bookDTOList);
        return ResponseEntity.ok(booksInBulk);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@Valid @PathVariable long id) throws BookException {
        BookDTO bookById = bookServiceImpl.getBookById(id);
        return  ResponseEntity.ok(bookById);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> getBookByISBN(@Valid @PathVariable String isbn) throws BookException {
        BookDTO bookByISBN = bookServiceImpl.getBookByISBN(isbn);
        return ResponseEntity.ok(bookByISBN);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookDTO> updateBook(@Valid @PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) throws BookException {
        BookDTO updatedBook = bookServiceImpl.updateBook(id, bookDTO);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@Valid @PathVariable Long id) throws BookException{
        bookServiceImpl.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book with id "+id+" deleted successfully",true));
    }

    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<ApiResponse> hardDeleteBook(@Valid @PathVariable Long id) throws BookException {
        bookServiceImpl.hardDeleteBook(id);
        return ResponseEntity.ok(new ApiResponse("Book with id "+id+" permanently deleted successfully",true));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookDTO>> searchBooks(
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false,defaultValue = "false") Boolean availableOnly,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ){
        BookSearchRequest searchRequest=new BookSearchRequest();
        searchRequest.setGenreId(genreId);
        searchRequest.setAvailableOnly(availableOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<BookDTO> bookDTOPageResponse = bookServiceImpl.searchBooksWithFilters(searchRequest);
        return ResponseEntity.ok(bookDTOPageResponse);
     }


    @PostMapping("/search")
    public ResponseEntity<PageResponse<BookDTO>> advancedSearch(@RequestBody BookSearchRequest bookSearchRequest){
        PageResponse<BookDTO> bookDTOPageResponse = bookServiceImpl.searchBooksWithFilters(bookSearchRequest);
        return ResponseEntity.ok(bookDTOPageResponse);
    }

    @GetMapping("/stats")
    public ResponseEntity<BookStatsResponse> getBookStats(){
        long totalActive=bookServiceImpl.getTotalActiveBooks();
        long totalAvailableBooks = bookServiceImpl.getTotalAvailableBooks();
        BookStatsResponse bookStatsResponse=new BookStatsResponse(totalActive,totalAvailableBooks);
        return ResponseEntity.ok(bookStatsResponse);
    }

    @Data
    @AllArgsConstructor
    public static class BookStatsResponse {
        private long totalActiveBooks;
        private long totalAvailableBooks;
    }
}
