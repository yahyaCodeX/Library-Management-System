package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.WishListDto;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.entity.Book;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.entity.WishList;
import com.librarymanagment.librarymanagment.exception.BookException;
import com.librarymanagment.librarymanagment.mapper.WishListMapper;
import com.librarymanagment.librarymanagment.repository.BookRepository;
import com.librarymanagment.librarymanagment.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService{
    private final WishListRepository wishListRepository;
    private final UserService userService;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final WishListMapper wishListMapper;

    @Override
    public WishListDto addToWishList(Long bookId) throws BookException {
        User user=userService.getCurrentUser();

        // 1. Validate Book exists
        Book book=bookRepository.findById(bookId)
                .orElseThrow(()->new BookException("Book not found with id: "+bookId));

        // 2. Check if already in wishlist
        if(wishListRepository.existsByUserIdAndBookId(user.getId(),bookId)){
            throw new BookException("Book is already in your WishList");
        }

        WishList wishList= WishList.builder()
                .user(user)
                .book(book)
                .build();
        WishList savedWishList=wishListRepository.save(wishList);
        return wishListMapper.toDto(savedWishList);
    }

    @Override
    public void removeFromWishList(Long bookId) throws Exception {
        User user=userService.getCurrentUser();

        WishList wishList = wishListRepository.findByUserIdAndBookId(user.getId(), bookId);

        if(wishList==null){
            throw new Exception("book is not in your wishlist");
        }

        wishListRepository.delete(wishList);

    }

    @Override
    public PageResponse<WishListDto> getMyWishList(int page, int size) {
        Pageable pageable= PageRequest.of(page,size, Sort.by("addedAt").descending());
        Page<WishList> wishListPage=wishListRepository.findByUserId(userService.getCurrentUser().getId(),pageable);

        return ConvertToPageResponse(wishListPage);
    }

    private PageResponse<WishListDto> ConvertToPageResponse(Page<WishList> wishListPage){
        List<WishListDto> wishListDto = wishListPage.getContent()
                .stream()
                .map(wishListMapper::toDto)
                .toList();

        return new PageResponse<>(
                wishListDto,
                wishListPage.getNumber(),
                wishListPage.getSize(),
                wishListPage.getTotalElements(),
                wishListPage.getTotalPages(),
                wishListPage.isLast(),
                wishListPage.isFirst(),
                wishListPage.isEmpty()
        );
    }
}
