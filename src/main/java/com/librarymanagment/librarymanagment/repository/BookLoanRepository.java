package com.librarymanagment.librarymanagment.repository;

import com.librarymanagment.librarymanagment.constant.BookLoanStatus;
import com.librarymanagment.librarymanagment.entity.Book;
import com.librarymanagment.librarymanagment.entity.BookLoan;
import com.librarymanagment.librarymanagment.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface BookLoanRepository extends JpaRepository<BookLoan,Long> {
    Page<BookLoan> findByUserId(Long userId, Pageable pageable);
    Page<BookLoan> findByStatusAndUser(BookLoanStatus status, User user, Pageable pageable);
    Page<BookLoan> findByStatus(BookLoanStatus status,Pageable pageable);

    @Query("Select count(bl) from BookLoan bl where bl.user.id=:userId and (bl.status='CHECKED_OUT' OR bl.status='OVERDUE')")
    long countActiveBookLoansByUser(@Param("userId") Long userId);

    @Query("Select bl from BookLoan bl where bl.returnDate<:currentDate and (bl.status='CHECKED_OUT' OR bl.status='OVERDUE')")
    Page<BookLoan> findOverdueBookLoans(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    Page<BookLoan> findByBookId(Long bookid, Pageable pageable);

    @Query("select bl from BookLoan bl where bl.checkoutDate Between :startDate and :endDate")
    Page<BookLoan> findBookLoansByDateRange(@Param("startDate")LocalDate startDate,@Param("endDate") LocalDate endDate, Pageable pageable);

    BookLoan book(Book book);
}
