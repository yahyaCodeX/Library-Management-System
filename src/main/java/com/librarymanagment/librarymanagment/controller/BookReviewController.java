package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.ApiResponse;
import com.librarymanagment.librarymanagment.dto.BookReviewDto;
import com.librarymanagment.librarymanagment.dto.request.BookReviewRequest;
import com.librarymanagment.librarymanagment.dto.request.UpdateReviewRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.service.BookReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class BookReviewController {
    private final BookReviewService bookReviewService;

    @PostMapping
    public ResponseEntity<BookReviewDto> createReview(@Valid @RequestBody BookReviewRequest request) throws Exception {
        BookReviewDto dto = bookReviewService.createBookReview(request);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<BookReviewDto> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        BookReviewDto dto = bookReviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long reviewId) {
        bookReviewService.deleteReview(reviewId);
        return ResponseEntity.ok(new ApiResponse("Review deleted successfully", true));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<BookReviewDto>> getReviewsByBookId(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<BookReviewDto> response = bookReviewService.getReviewsByBookId(bookId, page, size);
        return ResponseEntity.ok(response);
    }
}

