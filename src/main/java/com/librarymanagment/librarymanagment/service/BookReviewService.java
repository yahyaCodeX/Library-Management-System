package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.BookReviewDto;
import com.librarymanagment.librarymanagment.dto.request.BookReviewRequest;
import com.librarymanagment.librarymanagment.dto.request.UpdateReviewRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;

public interface BookReviewService {
    BookReviewDto createBookReview(BookReviewRequest request) throws Exception;
    BookReviewDto updateReview(Long reviewId, UpdateReviewRequest request);
    void deleteReview(Long reviewId);
    PageResponse<BookReviewDto> getReviewsByBookId(Long id,int page,int size);
}
