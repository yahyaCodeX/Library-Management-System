package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.BookDTO;
import com.librarymanagment.librarymanagment.entity.Book;
import com.librarymanagment.librarymanagment.entity.Genre;
import com.librarymanagment.librarymanagment.exception.BookException;
import com.librarymanagment.librarymanagment.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookMapper {
    private final GenreRepository genreRepository;

    public BookDTO toDTO(Book book){
        if(book==null){
            return null;
        }
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .genreId(book.getGenre().getId())
                .genreName(book.getGenre().getName())
                .genreCode(book.getGenre().getCode())
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate())
                .language(book.getLanguage())
                .pages(book.getPages())
                .description(book.getDescription())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .price(book.getPrice())
                .coverImageUrl(book.getCoverImageUrl())
                .active(book.getActive())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
    public Book toEntity(BookDTO bookDTO)throws BookException {
        if(bookDTO==null){
            return null;
        }
        Book book=new Book();
        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());

        if(bookDTO.getGenreId()!=null){
            Genre genre=genreRepository.findById(bookDTO.getGenreId())
                    .orElseThrow(()->new BookException("Genre with id "+bookDTO.getGenreId()+" not found"));
            book.setGenre(genre);
        }
        book.setPublisher(bookDTO.getPublisher());
        book.setPublishedDate(bookDTO.getPublishedDate());
        book.setLanguage(bookDTO.getLanguage());
        book.setPages(bookDTO.getPages());
        book.setDescription(bookDTO.getDescription());
        book.setTotalCopies(bookDTO.getTotalCopies());
        book.setAvailableCopies(bookDTO.getAvailableCopies());
        book.setPrice(bookDTO.getPrice());
        book.setCoverImageUrl(bookDTO.getCoverImageUrl());
        book.setActive(true);

        return book;
    }

    public void updateEntityFromDTO(BookDTO bookDTO,Book book)throws BookException {
        if(bookDTO==null || book==null){
            return;
        }
        //ISBN should not be updated
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());

        //update genre if provided
        if(bookDTO.getGenreId() != null){
            Genre genre=genreRepository.findById(bookDTO.getGenreId())
                    .orElseThrow(()->new BookException("Genre with id "+bookDTO.getGenreId()+" not found"));
            book.setGenre(genre);
        }

        book.setPublisher(bookDTO.getPublisher());
        book.setPublishedDate(bookDTO.getPublishedDate());
        book.setLanguage(bookDTO.getLanguage());
        book.setPages(bookDTO.getPages());
        book.setDescription(bookDTO.getDescription());
        book.setTotalCopies(bookDTO.getTotalCopies());
        book.setAvailableCopies(bookDTO.getAvailableCopies());
        book.setPrice(bookDTO.getPrice());
        book.setCoverImageUrl(bookDTO.getCoverImageUrl());

        if(bookDTO.getActive()!=null){
            book.setActive(bookDTO.getActive());
        }

}}
