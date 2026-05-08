package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.WishListDto;
import com.librarymanagment.librarymanagment.entity.WishList;
import org.springframework.stereotype.Component;

@Component
public class WishListMapper {
    private final BookMapper bookMapper;

    public WishListMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public WishListDto toDto(WishList wishList){
        if(wishList==null){
            return null;
        }
        WishListDto dto=new WishListDto();
        dto.setId(wishList.getId());

        if(wishList.getUser()!=null){
            dto.setUserId(wishList.getUser().getId());
            dto.setUserFullName(wishList.getUser().getFullName());
        }

        if(wishList.getBook()!=null){
            dto.setBook(bookMapper.toDTO(wishList.getBook()));
        }
        dto.setAddedAt(wishList.getAddedAt());

        return dto;
    }
}
