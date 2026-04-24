package com.librarymanagment.librarymanagment.repository;

import com.librarymanagment.librarymanagment.constant.ReservationStatus;
import com.librarymanagment.librarymanagment.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r " +
            "WHERE r.user.id = :userId AND r.book.id = :bookId " +
            "AND (r.reservationStatus = com.librarymanagment.librarymanagment.constant.ReservationStatus.PENDING OR r.reservationStatus = com.librarymanagment.librarymanagment.constant.ReservationStatus.AVAILABLE)")
    boolean hasActiveReservation(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId " +
            "AND (r.reservationStatus = com.librarymanagment.librarymanagment.constant.ReservationStatus.PENDING OR r.reservationStatus = com.librarymanagment.librarymanagment.constant.ReservationStatus.AVAILABLE)")
    long countActiveReservationsByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.book.id = :bookId " +
            "AND r.reservationStatus = com.librarymanagment.librarymanagment.constant.ReservationStatus.PENDING")
    long countPendingReservationsByBook(@Param("bookId") Long bookId);

    @Query("Select r from Reservation  r where (:userId is null or r.user.id=:userId) AND (:bookId is null or r.book.id=:bookId) AND (:status is null or r.reservationStatus=:status) AND (:activeOnly is false or r.reservationStatus=com.librarymanagment.librarymanagment.constant.ReservationStatus.PENDING or r.reservationStatus=com.librarymanagment.librarymanagment.constant.ReservationStatus.AVAILABLE)")
    Page<Reservation> searchReservationsWithFilters(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("status") ReservationStatus status,
            @Param("activeOnly") Boolean activeOnly,
            Pageable pageable
    );
}
