package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.BookReviewDto;
import com.librarymanagment.librarymanagment.dto.request.BookReviewRequest;
import com.librarymanagment.librarymanagment.entity.BookReview;
import org.springframework.stereotype.Component;

@Component
public class BookReviewMapper {

    public BookReviewDto toDto(BookReview bookReview){
        if(bookReview==null){
            return null;
        }
        return BookReviewDto.builder()
                .id(bookReview.getId())
                .userId(bookReview.getUser().getId())
                .userName(bookReview.getUser().getFullName())
                .bookId(bookReview.getBook().getId())
                .bookTitle(bookReview.getBook().getTitle())
                .rating(bookReview.getRating())
                .reviewText(bookReview.getReviewText())
                .title(bookReview.getTitle())
                .createdAt(bookReview.getCreatedAt())
                .updatedAt(bookReview.getUpdatedAt())
                .build();
    }
}
