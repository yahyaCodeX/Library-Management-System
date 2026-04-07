package com.librarymanagment.librarymanagment.entity;

import com.librarymanagment.librarymanagment.constant.FineStatus;
import com.librarymanagment.librarymanagment.constant.FineType;
import com.librarymanagment.librarymanagment.exception.PaymentException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private BookLoan bookLoan;

    private FineType fineType;

    @Column(nullable = false)
    private Long amount;

    private FineStatus fineStatus;

    @Column(length = 500)
    private String reason;

    @Column(length = 1000)
    private String note;

    @ManyToOne
    private User waivedBy;

    @Column(name = "waived_at")
    private LocalDateTime waivedAt;

    @Column(name = "waived_reason", length = 1000)
    private String waivedReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_id")
    private User processedBy;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(nullable = false,updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public void applyPayment(Long amount) {
        if(amount==null || amount<=0){
            throw new PaymentException("Payment amount must be greater than zero");
        }
        //update fine status based on payment amount
        this.fineStatus=FineStatus.PAID;
        this.paidAt=LocalDateTime.now();
    }

    public void waive(User admin,String reason) {
       this.fineStatus=FineStatus.WAIVED;
         this.waivedBy=admin;
            this.waivedAt=LocalDateTime.now();
            this.waivedReason=reason;
    }
}
