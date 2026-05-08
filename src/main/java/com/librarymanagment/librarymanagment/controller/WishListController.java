package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.dto.ApiResponse;
import com.librarymanagment.librarymanagment.dto.WishListDto;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.exception.BookException;
import com.librarymanagment.librarymanagment.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/wishlist")
public class WishListController {
    private final WishListService wishListService;

    @PostMapping("/add/{bookId}")
    public ResponseEntity<WishListDto> addToWishList(@PathVariable Long bookId) throws BookException {
        WishListDto wishListDto = wishListService.addToWishList(bookId);
        return ResponseEntity.ok(wishListDto);
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<ApiResponse> removeFromWishList(@PathVariable Long bookId) throws Exception {
        wishListService.removeFromWishList(bookId);
        return ResponseEntity.ok(new ApiResponse("Book removed from wishlist Successfully",true));
    }

    @GetMapping("/my-wishlist")
    public ResponseEntity<PageResponse<WishListDto>> getMyWishList(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size
    ){
        PageResponse<WishListDto> myWishList = wishListService.getMyWishList(page, size);
        return ResponseEntity.ok(myWishList);
    }
}
