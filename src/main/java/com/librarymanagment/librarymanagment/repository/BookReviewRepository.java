package com.librarymanagment.librarymanagment.repository;

import com.librarymanagment.librarymanagment.entity.Book;
import com.librarymanagment.librarymanagment.entity.BookReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReviewRepository extends JpaRepository<BookReview,Long> {
    Page<BookReview> findByBook(Book book, Pageable pageable);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}
