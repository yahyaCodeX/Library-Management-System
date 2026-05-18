package com.librarymanagment.librarymanagment.service.Implementations;

import com.librarymanagment.librarymanagment.constant.BookLoanStatus;
import com.librarymanagment.librarymanagment.dto.BookReviewDto;
import com.librarymanagment.librarymanagment.dto.request.BookReviewRequest;
import com.librarymanagment.librarymanagment.dto.request.UpdateReviewRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.entity.Book;
import com.librarymanagment.librarymanagment.entity.BookReview;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.mapper.BookReviewMapper;
import com.librarymanagment.librarymanagment.repository.BookLoanRepository;
import com.librarymanagment.librarymanagment.repository.BookRepository;
import com.librarymanagment.librarymanagment.repository.BookReviewRepository;
import com.librarymanagment.librarymanagment.service.BookReviewService;
import com.librarymanagment.librarymanagment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {
    private final UserService userService;
    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookReviewMapper bookReviewMapper;
    private final BookLoanRepository bookLoanRepository;

    @Override
    public BookReviewDto createBookReview(BookReviewRequest request) throws Exception {
        User user=userService.getCurrentUser();
        Book book=bookRepository.findById(request.getBookId())
                .orElseThrow(()->new Exception("book not found"));

        if(bookReviewRepository.existsByUserIdAndBookId(user.getId(),book.getId())){
            throw new Exception("book review already exists");
        }
        boolean hasReadBook=hasUserReadBook(user.getId(),book.getId());
        if(!hasReadBook){
            throw new Exception("you have not read this book yet");
        }
        BookReview bookReview=new BookReview();
        bookReview.setUser(user);
        bookReview.setBook(book);
        bookReview.setRating(request.getRating());
        bookReview.setReviewText(request.getReviewText());
        bookReview.setTitle(request.getReviewTitle());

        BookReview savedBookReview=bookReviewRepository.save(bookReview);
        return bookReviewMapper.toDto(savedBookReview);
    }

    private boolean hasUserReadBook(Long id, Long id1) {
        return bookLoanRepository.existsByUserIdAndBookIdAndStatus(id, id1, BookLoanStatus.RETURNED);
    }

    @Override
    public BookReviewDto updateReview(Long reviewId, UpdateReviewRequest request) {
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        User currentUser = userService.getCurrentUser();
        if (!bookReview.getUser().getId().equals(currentUser.getId())
                && !currentUser.getRoles().contains("ROLE_ADMIN")) {
            throw new RuntimeException("You can only update your own reviews");
        }

        bookReview.setRating(request.getRating());
        bookReview.setReviewText(request.getReviewText());
        bookReview.setTitle(request.getReviewTitle());

        BookReview savedReview = bookReviewRepository.save(bookReview);
        return bookReviewMapper.toDto(savedReview);
    }

    @Override
    public void deleteReview(Long reviewId) {
        BookReview bookReview = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        User currentUser = userService.getCurrentUser();
        if (!bookReview.getUser().getId().equals(currentUser.getId())
                && !currentUser.getRoles().contains("ROLE_ADMIN")) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        bookReviewRepository.delete(bookReview);
    }

    @Override
    public PageResponse<BookReviewDto> getReviewsByBookId(Long id, int page, int size) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BookReview> reviewPage = bookReviewRepository.findByBook(book, pageable);

        return new PageResponse<>(
                reviewPage.getContent().stream().map(bookReviewMapper::toDto).toList(),
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast(),
                reviewPage.isFirst(),
                reviewPage.isEmpty()
        );
    }
}
