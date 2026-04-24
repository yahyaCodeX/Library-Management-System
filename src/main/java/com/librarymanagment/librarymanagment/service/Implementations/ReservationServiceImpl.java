package com.librarymanagment.librarymanagment.service.Implementations;

import com.librarymanagment.librarymanagment.constant.BookLoanStatus;
import com.librarymanagment.librarymanagment.constant.ReservationStatus;
import com.librarymanagment.librarymanagment.dto.ReservationDto;
import com.librarymanagment.librarymanagment.dto.request.CheckoutRequest;
import com.librarymanagment.librarymanagment.dto.request.ReservationRequest;
import com.librarymanagment.librarymanagment.dto.request.ReservationSearchRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.entity.Book;
import com.librarymanagment.librarymanagment.entity.Reservation;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.mapper.ReservationMapper;
import com.librarymanagment.librarymanagment.repository.BookLoanRepository;
import com.librarymanagment.librarymanagment.repository.BookRepository;
import com.librarymanagment.librarymanagment.repository.ReservationRepository;
import com.librarymanagment.librarymanagment.service.BookLoanService;
import com.librarymanagment.librarymanagment.service.ReservationService;
import com.librarymanagment.librarymanagment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookLoanRepository bookLoanRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final ReservationMapper reservationMapper;
    private final BookLoanService bookLoanService;

    int MAX_RESERVATIONS=5;

    @Override
    public ReservationDto createReservation(ReservationRequest reservationRequest) throws Exception {
        User user=userService.getCurrentUser();
        return createReservationForUser(reservationRequest,user.getId());
    }

    @Override
    public ReservationDto createReservationForUser(ReservationRequest request, Long userId) throws Exception {
        boolean alreadyHasLoan=bookLoanRepository.existsByUserIdAndBookIdAndStatus(userId,request.getBookId(), BookLoanStatus.CHECKED_OUT);
        if(alreadyHasLoan){
            throw new Exception(" User already has an active loan for this book");
        }
        //validate user
        User user=userService.getCurrentUser();

        //validate Book exists
        Book book=bookRepository.findById(request.getBookId()).
                orElseThrow(()-> new Exception("Book Not Found"));

        //check reservation
        if(reservationRepository.hasActiveReservation(userId,book.getId())){
            throw new Exception(" User has already an active reservation for this book");
        }
        //check if book is already available
        if(book.getAvailableCopies()>0){
            throw new Exception(" Book is already available");
        }
        //check user's active reservation limit
        long activeReservation=reservationRepository.countActiveReservationsByUser(userId);

        if(activeReservation>=MAX_RESERVATIONS){
            throw new Exception("you have reserved "+MAX_RESERVATIONS+ " times");
        }
        //create Reservation
        Reservation reservation=new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setNotificationSent(false);
        reservation.setNotes(request.getNotes());

        long pendingCount=reservationRepository.countPendingReservationsByBook(book.getId());
        reservation.setQueuePosition((int) pendingCount+1);
        Reservation savedReservation=reservationRepository.save(reservation);

        return reservationMapper.toDto(savedReservation);
    }

    @Override
    public ReservationDto cancelReservation(Long reservationId) throws Exception {
        Reservation reservation=reservationRepository.findById(reservationId).
                orElseThrow(()-> new RuntimeException("Reservation Not Found"));

        //verify current user owns this reservation(or admin)
        User currentUser=userService.getCurrentUser();
        if(!reservation.getUser().getId().equals(currentUser.getId()) && !currentUser.getRoles().contains("ROLE_ADMIN")){
            throw new Exception("You can only cancel your own reservations");
        }

        if(!reservation.canBeCancelled()){
            throw new Exception("This reservation cannot be cancelled");
        }

        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());

        Reservation savedReservation=reservationRepository.save(reservation);

        return reservationMapper.toDto(savedReservation);
    }

    @Override
    public ReservationDto fulfillReservation(Long reservationId) throws Exception {
        Reservation reservation=reservationRepository.findById(reservationId).
                orElseThrow(()-> new RuntimeException("Reservation Not Found"));

        if(reservation.getBook().getAvailableCopies()<=0){
            throw new Exception("Book is not available to fulfill this reservation");
        }

        reservation.setReservationStatus(ReservationStatus.FULLFILLED);
        reservation.setFulfilledAt(LocalDateTime.now());

        Reservation savedReservation=reservationRepository.save(reservation);

        CheckoutRequest request=new CheckoutRequest();
        request.setBookId(reservation.getBook().getId());
        request.setNotes("Auto checkout from reservation fulfillment");

        bookLoanService.checkoutBookForUser(reservation.getUser().getId(),request);
        return reservationMapper.toDto(savedReservation);
    }

    @Override
    public PageResponse<ReservationDto> getMyReservation(ReservationSearchRequest searchRequest) {
        User user=userService.getCurrentUser();
        searchRequest.setUserId(user.getId());
        return searchReservation(searchRequest);
    }

    @Override
    public PageResponse<ReservationDto> searchReservation(ReservationSearchRequest searchRequest) {
        Pageable pageable=createPageable(searchRequest);
        Page<Reservation> reservationPage=reservationRepository.searchReservationsWithFilters(
                searchRequest.getUserId(),
                searchRequest.getBookId(),
                searchRequest.getStatus(),
                searchRequest.getActiveOnly() != null ? searchRequest.getActiveOnly(): false,
                pageable
        );
        return buildPageResponse(reservationPage);
    }

    private PageResponse<ReservationDto> buildPageResponse(Page<Reservation> reservationPage) {
        List<ReservationDto> dtos=reservationPage.getContent().stream()
                .map(reservationMapper::toDto)
                .toList();

        PageResponse<ReservationDto> response=new PageResponse<>();
        response.setContent(dtos);
        response.setPageNumber(reservationPage.getNumber());
        response.setPageSize(reservationPage.getSize());
        response.setTotalElements(reservationPage.getTotalElements());
        response.setTotalPages(reservationPage.getTotalPages());
        response.setLast(reservationPage.isLast());
        return response;

    }

    private Pageable createPageable(ReservationSearchRequest searchRequest){
        Sort sort="ASC".equalsIgnoreCase(searchRequest.getSortDirection())
                ? Sort.by(searchRequest.getSortBy()).ascending()
                : Sort.by(searchRequest.getSortBy()).descending();

        return PageRequest.of(searchRequest.getPage(),searchRequest.getSize(),sort);
    }
}
