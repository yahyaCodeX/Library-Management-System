package com.librarymanagment.librarymanagment.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WishListDto {
    private Long id;
    private Long userId;
    private String userFullName;
    private BookDTO book;
    private LocalDateTime addedAt;
}
