package com.librarymanagment.librarymanagment.repository;

import com.librarymanagment.librarymanagment.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {

    @Query("""
            SELECT s FROM Subscription s
            WHERE s.user.id = :userId
            AND s.isActive = true
            AND s.startDate <= :today
            AND s.endDate >= :today
            """)
    Optional<Subscription> findActiveSubscriptionByUserId(
            @Param("userId") Long userId,
            @Param("today")LocalDate today
            );

    @Query("select s from Subscription s where s.isActive = true and s.endDate < :today")
    List<Subscription> findExpiredActiveSubscriptions(
            @Param("today") LocalDate today
    );

}
