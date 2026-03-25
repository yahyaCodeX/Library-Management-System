package com.librarymanagment.librarymanagment.dto.request;

import com.librarymanagment.librarymanagment.constant.BookLoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLoanSearchRequest {
    private Long userid;
    private Long bookid;
    private BookLoanStatus status;
    private Boolean overdueOnly;
    private Boolean unpaidFinesOnly;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer page=0;
    private Integer size=20;
    private String sortBy="createdAt";
    private String sortDirection="DESC";
}
