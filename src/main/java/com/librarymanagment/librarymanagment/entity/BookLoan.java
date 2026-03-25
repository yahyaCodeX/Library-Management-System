package com.librarymanagment.librarymanagment.entity;

import com.librarymanagment.librarymanagment.constant.BookLoanStatus;
import com.librarymanagment.librarymanagment.constant.BookLoanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Book book;

    private BookLoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private BookLoanStatus status;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Column(nullable = false)
    private LocalDate returnDate;

    @Column(nullable = false)
    private Integer renewalCount=0;

    @Column(nullable = false)
    private Integer maxRenewals=2;

    //fine todo

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private Boolean isOverdue=false;

    @Column(nullable = false)
    private Integer overdueDays=0;

    @Column(nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
