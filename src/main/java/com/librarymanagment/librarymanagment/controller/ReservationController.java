package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.constant.ReservationStatus;
import com.librarymanagment.librarymanagment.dto.ReservationDto;
import com.librarymanagment.librarymanagment.dto.request.ReservationRequest;
import com.librarymanagment.librarymanagment.dto.request.ReservationSearchRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@Valid @RequestBody ReservationRequest reservationRequest) throws Exception {
        ReservationDto reservationDto=reservationService.createReservation(reservationRequest);
        return ResponseEntity.ok(reservationDto);
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationDto> createReservationForUser(@PathVariable Long userId,@Valid @RequestBody ReservationRequest reservationRequest) throws Exception {
        ReservationDto reservationForUser = reservationService.createReservationForUser(reservationRequest, userId);
        return new ResponseEntity<>(reservationForUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationDto> cancelReservation(@PathVariable Long reservationId) throws Exception {
        ReservationDto reservationDto = reservationService.cancelReservation(reservationId);
        return  ResponseEntity.ok(reservationDto);
    }

    @PostMapping("/{reservationId}/fulfill")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationDto> fulfillReservation(@PathVariable Long reservationId) throws Exception {
        ReservationDto reservationDto = reservationService.fulfillReservation(reservationId);
        return  ResponseEntity.ok(reservationDto);
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<ReservationDto>> getMyReservations(
            @RequestParam(required = false)ReservationStatus status,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "reservedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
            )throws Exception{
        ReservationSearchRequest request=new ReservationSearchRequest();
        request.setStatus(status);
        request.setActiveOnly(activeOnly);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        PageResponse<ReservationDto> myReservation = reservationService.getMyReservation(request);
        return ResponseEntity.ok(myReservation);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<ReservationDto>> searchReservations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false)ReservationStatus status,
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "reservedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    )throws Exception{
        ReservationSearchRequest request=new ReservationSearchRequest();
        request.setUserId(userId);
        request.setBookId(bookId);
        request.setStatus(status);
        request.setActiveOnly(activeOnly);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        PageResponse<ReservationDto> myReservation = reservationService.searchReservation(request);
        return ResponseEntity.ok(myReservation);
    }


}
