package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.constant.BookLoanStatus;
import com.librarymanagment.librarymanagment.dto.BookLoanDto;
import com.librarymanagment.librarymanagment.dto.request.BookLoanSearchRequest;
import com.librarymanagment.librarymanagment.dto.request.CheckinRequest;
import com.librarymanagment.librarymanagment.dto.request.CheckoutRequest;
import com.librarymanagment.librarymanagment.dto.request.RenewalRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.exception.BookException;

public interface BookLoanService {
    BookLoanDto checkoutBook(CheckoutRequest checkoutRequest);

    BookLoanDto checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws BookException;

    BookLoanDto checkinBook(CheckinRequest checkinRequest);

    BookLoanDto renewCheckout(RenewalRequest renewalRequest);

    PageResponse<BookLoanDto> getMyBookLoans(BookLoanStatus status,int page,int size);

    PageResponse<BookLoanDto> getBookLoans(BookLoanSearchRequest request);

    int updateOverdueBookLoan();
}