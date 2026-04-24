package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.ReservationDto;
import com.librarymanagment.librarymanagment.dto.request.ReservationRequest;
import com.librarymanagment.librarymanagment.dto.request.ReservationSearchRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;

public interface ReservationService {
    ReservationDto createReservation(ReservationRequest reservationRequest) throws Exception;
    ReservationDto createReservationForUser(ReservationRequest request,Long userId) throws Exception;
    ReservationDto cancelReservation(Long reservationId) throws Exception;
    ReservationDto fulfillReservation(Long reservationId) throws Exception;

    PageResponse<ReservationDto> getMyReservation(ReservationSearchRequest searchRequest);
    PageResponse<ReservationDto> searchReservation(ReservationSearchRequest searchRequest);
}
