package com.librarymanagment.librarymanagment.repository;

import com.librarymanagment.librarymanagment.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan,Long> {
        boolean existsByPlanCode(String planCode);

}
