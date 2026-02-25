package com.librarymanagment.librarymanagment.repository;

import com.librarymanagment.librarymanagment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRespository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);

}
