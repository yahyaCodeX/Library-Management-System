package com.librarymanagment.librarymanagment.service.Implementations;

import com.librarymanagment.librarymanagment.dto.BookDTO;
import com.librarymanagment.librarymanagment.dto.request.BookSearchRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.entity.Book;
import com.librarymanagment.librarymanagment.exception.BookException;
import com.librarymanagment.librarymanagment.mapper.BookMapper;
import com.librarymanagment.librarymanagment.repository.BookRepository;
import com.librarymanagment.librarymanagment.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDTO createBook(BookDTO bookDTO) throws BookException {
        if(bookRepository.existsByIsbn(bookDTO.getIsbn())){
            throw new BookException("Book with ISBN "+bookDTO.getIsbn()+" already exists");
        }
        Book book=bookMapper.toEntity(bookDTO);
        //check if available copies is less than or equal to total copies
        book.isAvailableCopiesValid();
        Book savedBook=bookRepository.save(book);
        return bookMapper.toDTO(savedBook);
    }

    @Override
    public List<BookDTO> createBooksInBulk(List<BookDTO> bookDTOList) throws BookException {
        List<BookDTO> createdBooks=new ArrayList<>();
        for(BookDTO bookDTO:bookDTOList){
            BookDTO book=createBook(bookDTO);
            createdBooks.add(book);
        }
        return createdBooks;
    }

    @Override
    public BookDTO getBookById(Long id) throws BookException {
        Book book=bookRepository.findById(id)
                .orElseThrow(()-> new BookException("Book with id "+id+" not found"));
        return bookMapper.toDTO(book);
    }

    @Override
    public BookDTO getBookByISBN(String isbn) throws BookException {
        Book book=bookRepository.findByIsbn(isbn)
                .orElseThrow(()-> new BookException("Book with id "+isbn+" not found"));
        return bookMapper.toDTO(book);
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) throws BookException {
        Book existingBook=bookRepository.findById(id)
                .orElseThrow(()-> new BookException("Book with id "+id+" not found"));
        bookMapper.updateEntityFromDTO(bookDTO, existingBook);
        existingBook.isAvailableCopiesValid();
        Book updatedBook=bookRepository.save(existingBook);
        return bookMapper.toDTO(updatedBook);
    }

    @Override
    public void deleteBook(Long id) throws BookException {
        Book existingBook=bookRepository.findById(id)
                .orElseThrow(()-> new BookException("Book with id "+id+" not found"));
        existingBook.setActive(false);
        bookRepository.save(existingBook);
    }

    @Override
    public void hardDeleteBook(Long id) throws BookException {
        Book existingBook=bookRepository.findById(id)
                .orElseThrow(()-> new BookException("Book with id "+id+" not found"));
        bookRepository.delete(existingBook);
    }

    @Override
    public PageResponse<BookDTO> searchBooksWithFilters(BookSearchRequest searchRequest) {
        Pageable pageable = createPageable(
                searchRequest.getPage(),
                searchRequest.getSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortDirection()
        );

        Page<Book> books = bookRepository.searchBooksWithFilters(
                searchRequest.getSearchTerm(),
                searchRequest.getGenreId(),
                searchRequest.getAvailableOnly(),
                pageable
        );

        return convertToPageResponse(books);
    }

    @Override
    public long getTotalActiveBooks() {
        return bookRepository.countByActiveTrue();
    }

    @Override
    public long getTotalAvailableBooks() {
        return bookRepository.countAvailableBooks();
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection){
        size=Math.min(size,10);
        size=Math.max(size,1);

        Sort sort=sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page,size,sort);
    }

    private PageResponse<BookDTO> convertToPageResponse(Page<Book> books){
        List<BookDTO> bookDTOS=books.getContent()
                .stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());
        return new PageResponse<>(bookDTOS,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isLast(),
                books.isFirst(),
                books.isEmpty());
    }
}
