package com.librarymanagment.librarymanagment.exception;

/**
 * Custom exception for BookLoan-related errors
 * Used for all book loan transaction exceptions such as:
 * - Invalid checkout/checkin operations
 * - Renewal limit exceeded
 * - User subscription violations
 * - Overdue book constraints
 */
public class BookLoanException extends RuntimeException {

    public BookLoanException(String message) {
        super(message);
    }

    public BookLoanException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookLoanException(Throwable cause) {
        super(cause);
    }
}

