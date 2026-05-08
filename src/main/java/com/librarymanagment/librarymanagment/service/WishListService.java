package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.WishListDto;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.exception.BookException;

public interface WishListService {
    WishListDto addToWishList(Long bookId) throws BookException;
    void removeFromWishList(Long bookId) throws Exception;
    PageResponse<WishListDto> getMyWishList(int page,int size);
}
