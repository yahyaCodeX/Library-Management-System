package com.librarymanagment.librarymanagment.repository;

import com.librarymanagment.librarymanagment.entity.Book;
import com.librarymanagment.librarymanagment.entity.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList,Long> {
    Page<WishList> findByUserId(Long userid, Pageable pageable);
    WishList findByUserIdAndBookId(Long userId,Long bookId);
    boolean existsByUserIdAndBookId(Long userId,Long bookId);

}
