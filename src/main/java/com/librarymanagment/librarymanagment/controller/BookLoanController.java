package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.constant.BookLoanStatus;
import com.librarymanagment.librarymanagment.dto.ApiResponse;
import com.librarymanagment.librarymanagment.dto.BookLoanDto;
import com.librarymanagment.librarymanagment.dto.request.*;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.service.Implementations.BookLoanServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-loans")
public class BookLoanController {
    private final BookLoanServiceImpl bookLoanService;

    @PostMapping("/checkout")
    public ResponseEntity<BookLoanDto> checkout(@Valid @RequestBody CheckoutRequest checkoutRequest){
        BookLoanDto bookLoanDto = bookLoanService.checkoutBook(checkoutRequest);
        return new ResponseEntity<>(bookLoanDto, HttpStatus.CREATED);
    }

    @PostMapping("/checkout/user/{userId}")
    public ResponseEntity<BookLoanDto> checkoutBookForUser(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest checkoutRequest){
        BookLoanDto bookLoanDto = bookLoanService.checkoutBookForUser(userId, checkoutRequest);
        return new ResponseEntity<>(bookLoanDto, HttpStatus.CREATED);
    }

    @PostMapping("/checkin")
   public ResponseEntity<BookLoanDto> checkin(@Valid @RequestBody CheckinRequest checkinRequest){
        BookLoanDto bookLoanDto = bookLoanService.checkinBook(checkinRequest);
        return new ResponseEntity<>(bookLoanDto,HttpStatus.CREATED);
    }

    @PostMapping("/renew")
    public ResponseEntity<BookLoanDto> renew(@Valid @RequestBody RenewalRequest renewalRequest){
        BookLoanDto bookLoanDto = bookLoanService.renewCheckout(renewalRequest);
        return new ResponseEntity<>(bookLoanDto,HttpStatus.OK);
    }

    @GetMapping("/MyBook-Loans")
    public ResponseEntity<PageResponse<BookLoanDto>> MyBookLoans(
            @RequestParam(required = false)BookLoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
            ){
        PageResponse<BookLoanDto> myBookLoans = bookLoanService.getMyBookLoans(status, page, size);
        return new ResponseEntity<>(myBookLoans, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookLoanDto>> getAllBookLoans(@RequestBody BookLoanSearchRequest searchRequest){
        PageResponse<BookLoanDto> bookLoans = bookLoanService.getBookLoans(searchRequest);
        return new ResponseEntity<>(bookLoans, HttpStatus.OK);
    }

    @PostMapping("/admin/update-overdue")
    public ResponseEntity<ApiResponse> updateOverdueBookLoans(){
        int updatedCount = bookLoanService.updateOverdueBookLoan();
        return new ResponseEntity<>(new ApiResponse("overdue book loans are updated successfully", true), HttpStatus.OK);
    }



}
