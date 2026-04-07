package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.FineDto;
import com.librarymanagment.librarymanagment.entity.Fine;
import org.springframework.stereotype.Component;

@Component
public class FineMapper {
    public FineDto toDto(Fine fine){
        if(fine==null) return null;

        FineDto dto=new FineDto();
        dto.setId(fine.getId());

        //Book loan details
        if(fine.getBookLoan()!=null){
            dto.setBookLoanId(fine.getBookLoan().getId());
            if(fine.getBookLoan().getBook()!=null){
                dto.setBookTitle(fine.getBookLoan().getBook().getTitle());
                dto.setBookIsbn(fine.getBookLoan().getBook().getIsbn());
            }
        }

        //User details
        if(fine.getUser()!=null){
            dto.setUserId(fine.getUser().getId());
            dto.setUserName(fine.getUser().getFullName());
            dto.setUserEmail(fine.getUser().getEmail());
        }

        dto.setFineType(fine.getFineType());
        dto.setAmount(fine.getAmount());
        dto.setFineStatus(fine.getFineStatus());
        dto.setReason(fine.getReason());
        dto.setNotes(fine.getNote());

            //Waiver details
        if(fine.getWaivedBy()!=null){
            dto.setWaivedByUserId(fine.getWaivedBy().getId());
            dto.setWaivedByUserName(fine.getWaivedBy().getFullName());
        }
        dto.setWaivedAt(fine.getWaivedAt());
        dto.setWaivedReason(fine.getWaivedReason());

        //payment details
        dto.setPaidAt(fine.getPaidAt());
        if(fine.getProcessedBy() != null){
            dto.setProcessedByUserId(fine.getProcessedBy().getId());
            dto.setProcessedByUserName(fine.getProcessedBy().getFullName());
        }

        dto.setTransactionId(fine.getTransactionId());

        dto.setCreatedAt(fine.getCreatedAt());
        dto.setUpdatedAt(fine.getUpdatedAt());

        return dto;

    }
}
