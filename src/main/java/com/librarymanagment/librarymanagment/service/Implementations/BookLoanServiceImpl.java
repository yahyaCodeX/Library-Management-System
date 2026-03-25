package com.librarymanagment.librarymanagment.service.Implementations;

import com.librarymanagment.librarymanagment.constant.BookLoanStatus;
import com.librarymanagment.librarymanagment.constant.BookLoanType;
import com.librarymanagment.librarymanagment.dto.BookLoanDto;
import com.librarymanagment.librarymanagment.dto.SubscriptionDto;
import com.librarymanagment.librarymanagment.dto.request.BookLoanSearchRequest;
import com.librarymanagment.librarymanagment.dto.request.CheckinRequest;
import com.librarymanagment.librarymanagment.dto.request.CheckoutRequest;
import com.librarymanagment.librarymanagment.dto.request.RenewalRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.entity.Book;
import com.librarymanagment.librarymanagment.entity.BookLoan;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.exception.BookException;
import com.librarymanagment.librarymanagment.exception.BookLoanException;
import com.librarymanagment.librarymanagment.mapper.BookLoanMapper;
import com.librarymanagment.librarymanagment.repository.BookLoanRepository;
import com.librarymanagment.librarymanagment.repository.BookRepository;
import com.librarymanagment.librarymanagment.repository.UserRespository;
import com.librarymanagment.librarymanagment.service.BookLoanService;
import com.librarymanagment.librarymanagment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookLoanServiceImpl implements BookLoanService {
    private final BookLoanRepository bookLoanRepository;
    private final BookRepository bookRepository;
    private final UserRespository userRepository;
    private final SubscriptionServiceImpl subscriptionService;
    private final UserService userService;
    private final BookLoanMapper mapper;

    @Override
    @Transactional
    public BookLoanDto checkoutBook(CheckoutRequest checkoutRequest) throws BookLoanException {
        User user = userService.getCurrentUser();
        return checkoutBookForUser(user.getId(), checkoutRequest);
    }

    @Override
    @Transactional
    public BookLoanDto checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws BookLoanException {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BookLoanException("User with ID " + userId + " not found"));

        // Check user has active subscription
        SubscriptionDto subscriptionDto;
        try {
            subscriptionDto = subscriptionService.getUsersActiveSubscription(userId);
        } catch (Exception e) {
            throw new BookLoanException("User with ID " + userId + " does not have an active subscription");
        }

        // Validate book exists
        Book book = bookRepository.findById(checkoutRequest.getBookId())
                .orElseThrow(() -> new BookLoanException("Book with ID " + checkoutRequest.getBookId() + " not found"));

        // Check book is active
        if (!book.getActive()) {
            throw new BookLoanException("Book with ID " + checkoutRequest.getBookId() + " is not active");
        }

        // Check if book has available copies
        if (book.getAvailableCopies() <= 0) {
            throw new BookLoanException("No available copies of this book");
        }

        // Check if user already has an active checkout of this book
        List<BookLoan> existingLoans = bookLoanRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        boolean hasActiveCheckout = existingLoans.stream()
                .anyMatch(loan -> loan.getBook().getId().equals(checkoutRequest.getBookId())
                        && loan.getStatus() == BookLoanStatus.CHECKED_OUT);

        if (hasActiveCheckout) {
            throw new BookLoanException("User already has an active checkout for this book");
        }

        // Check user's Active Checkout limit
        long activeCheckouts = bookLoanRepository.countActiveBookLoansByUser(userId);
        int maxBookAllowed = subscriptionDto.getMaxBooksAllowed();
        if (activeCheckouts >= maxBookAllowed) {
            throw new BookLoanException("User has reached the maximum active checkout limit of " + maxBookAllowed + " books");
        }

        // Check if user has overdue books
        long overdueCount = existingLoans.stream()
                .filter(loan -> loan.getStatus() == BookLoanStatus.OVERDUE)
                .count();
        if (overdueCount > 0) {
            throw new BookLoanException("User has overdue books and cannot checkout new books until they are returned");
        }

        // Set checkout date (use provided date or default to today)
        LocalDate checkoutDate = checkoutRequest.getCheckoutDate() != null
                ? checkoutRequest.getCheckoutDate()
                : LocalDate.now();

        // Set due date (use provided date or calculate from lending period)
        LocalDate dueDate = checkoutRequest.getDueDate() != null
                ? checkoutRequest.getDueDate()
                : checkoutDate.plusDays(14); // Default 14-day lending period

        // Create new book loan
        BookLoan bookLoan = BookLoan.builder()
                .user(user)
                .book(book)
                .loanType(BookLoanType.CHECKOUT)
                .status(BookLoanStatus.CHECKED_OUT)
                .checkoutDate(checkoutDate)
                .returnDate(dueDate)
                .renewalCount(0)
                .maxRenewals(2)
                .notes(checkoutRequest.getNotes())
                .isOverdue(false)
                .overdueDays(0)
                .build();

        // Save the loan
        BookLoan savedLoan = bookLoanRepository.save(bookLoan);

        // Decrement available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return mapper.mapToDto(savedLoan);
    }

    @Override
    @Transactional
    public BookLoanDto checkinBook(CheckinRequest checkinRequest) throws BookLoanException {
        // Find the book loan
        BookLoan bookLoan = bookLoanRepository.findById(checkinRequest.getBookLoanId())
                .orElseThrow(() -> new BookLoanException("Book loan with ID " + checkinRequest.getBookLoanId() + " not found"));

        // Check if book is already returned
        if (bookLoan.getStatus() == BookLoanStatus.RETURNED) {
            throw new BookLoanException("This book has already been returned");
        }

        // Store original due date for overdue calculation BEFORE updating returnDate
        LocalDate originalDueDate = bookLoan.getReturnDate();
        LocalDate returnedToday = LocalDate.now();

        // Check if book is overdue (before updating returnDate)
        if (returnedToday.isAfter(originalDueDate)) {
            long overdueDays = ChronoUnit.DAYS.between(originalDueDate, returnedToday);
            bookLoan.setIsOverdue(true);
            bookLoan.setOverdueDays((int) overdueDays);
        } else {
            bookLoan.setIsOverdue(false);
            bookLoan.setOverdueDays(0);
        }

        // Update loan status and actual return date
        bookLoan.setStatus(BookLoanStatus.RETURNED);
        bookLoan.setReturnDate(returnedToday);

        if (checkinRequest.getNotes() != null) {
            bookLoan.setNotes(checkinRequest.getNotes());
        }

        // Save the updated loan
        BookLoan updatedLoan = bookLoanRepository.save(bookLoan);

        // Increment available copies (if book not lost)
        if (!bookLoan.getStatus().equals(BookLoanStatus.LOST)) {
            Book book = bookLoan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }

        return mapper.mapToDto(updatedLoan);
    }

    @Override
    @Transactional
    public BookLoanDto renewCheckout(RenewalRequest renewalRequest) throws BookLoanException {
        // Find the book loan
        BookLoan bookLoan = bookLoanRepository.findById(renewalRequest.getBookLoanId())
                .orElseThrow(() -> new BookLoanException("Book loan with ID " + renewalRequest.getBookLoanId() + " not found"));

        // Check if book is already returned
        if (bookLoan.getStatus() == BookLoanStatus.RETURNED) {
            throw new BookLoanException("Cannot renew a returned book");
        }

        // Check renewal limit
        if (bookLoan.getRenewalCount() >= bookLoan.getMaxRenewals()) {
            throw new BookLoanException("Maximum renewal limit reached");
        }

        // Calculate new due date
        int extensionDays = renewalRequest.getExtensionDays() != null
                ? renewalRequest.getExtensionDays()
                : 14;

        LocalDate newDueDate = bookLoan.getReturnDate().plusDays(extensionDays);

        // Update loan
        bookLoan.setReturnDate(newDueDate);
        bookLoan.setRenewalCount(bookLoan.getRenewalCount() + 1);
        bookLoan.setLoanType(BookLoanType.RENEWAL);
        bookLoan.setIsOverdue(false);
        bookLoan.setOverdueDays(0);

        if (renewalRequest.getNotes() != null) {
            bookLoan.setNotes(renewalRequest.getNotes());
        }

        BookLoan updatedLoan = bookLoanRepository.save(bookLoan);
        return mapper.mapToDto(updatedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookLoanDto> getMyBookLoans(BookLoanStatus status, int page, int size) {
        User currentUser = userService.getCurrentUser();
        Page<BookLoan> bookLoanPage;

        if (status != null) {
            // Return only loans with specific status, sorted by due date
            Pageable pageable = PageRequest.of(page, size, Sort.by("returnDate").ascending());
            bookLoanPage = bookLoanRepository.findByStatusAndUser(status, currentUser, pageable);
        } else {
            // Return all book loans for the user, sorted by checkout date
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            bookLoanPage = bookLoanRepository.findByUserId(currentUser.getId(), pageable);
        }

        return convertToPageResponse(bookLoanPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookLoanDto> getBookLoans(BookLoanSearchRequest request) {
        Sort.Direction direction = Sort.Direction.fromString(request.getSortDirection().toUpperCase());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
                Sort.by(direction, request.getSortBy()));

        Page<BookLoan> page;

        // Apply filters based on search request
        if (Boolean.TRUE.equals(request.getOverdueOnly())) {
            page = bookLoanRepository.findOverdueBookLoans(LocalDate.now(), pageable);
        } else if (request.getUserid() != null) {
            page = bookLoanRepository.findByUserId(request.getUserid(), pageable);
        } else if (request.getBookid() != null) {
            page = bookLoanRepository.findByBookId(request.getBookid(), pageable);
        } else if (request.getStatus() != null) {
            page = bookLoanRepository.findByStatus(request.getStatus(), pageable);
        } else if (request.getStartDate() != null && request.getEndDate() != null) {
            page = bookLoanRepository.findBookLoansByDateRange(request.getStartDate(), request.getEndDate(), pageable);
        } else {
            page = bookLoanRepository.findAll(pageable);
        }

        return convertToPageResponse(page);
    }

    @Override
    @Transactional
    public int updateOverdueBookLoan() {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<BookLoan> overduePage = bookLoanRepository.findOverdueBookLoans(LocalDate.now(), pageable);

        int updateCount = 0;
        for (BookLoan bookLoan : overduePage.getContent()) {
            if (bookLoan.getStatus() == BookLoanStatus.CHECKED_OUT) {
                bookLoan.setStatus(BookLoanStatus.OVERDUE);
                bookLoan.setIsOverdue(true);

                // Calculate overdue days (difference between today and due date)
                long overdueDays = ChronoUnit.DAYS.between(bookLoan.getReturnDate(), LocalDate.now());
                bookLoan.setOverdueDays((int) overdueDays);

                // TODO: Calculate fine based on overdue days

                bookLoanRepository.save(bookLoan);
                updateCount++;
            }
        }

        return updateCount;
    }
    /**
     * Helper method to create Pageable with size limits and sorting
     */
    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        // Ensure size is between 1 and 100
        size = Math.min(size, 100);
        size = Math.max(size, 1);

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }

    /**
     * Helper method to convert BookLoan Page to BookLoanDto PageResponse
     */
    private PageResponse<BookLoanDto> convertToPageResponse(Page<BookLoan> bookLoanPage) {
        List<BookLoanDto> dtos = bookLoanPage.getContent().stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());

        return new PageResponse<>(dtos,
                bookLoanPage.getNumber(),
                bookLoanPage.getSize(),
                bookLoanPage.getTotalElements(),
                bookLoanPage.getTotalPages(),
                bookLoanPage.isLast(),
                bookLoanPage.isFirst(),
                bookLoanPage.isEmpty());
    }

}
