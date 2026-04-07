package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.constant.FineStatus;
import com.librarymanagment.librarymanagment.constant.FineType;
import com.librarymanagment.librarymanagment.dto.FineDto;
import com.librarymanagment.librarymanagment.dto.request.WaiveFineRequest;
import com.librarymanagment.librarymanagment.dto.request.createFineRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.dto.response.PaymentInitiateResponse;

import java.util.List;

public interface FineService {
    FineDto createFine(createFineRequest request);
    PaymentInitiateResponse payFine(Long fineId, String transactionId);
    void markFineAsPaid(Long fineId,Long amount,String transactionId);
    FineDto waiveFine(WaiveFineRequest request);
    List<FineDto> getMyFines(FineStatus status, FineType type);
    PageResponse<FineDto> getAllFines(
            FineStatus status,
            FineType type,
            Long userId,
            int page,
            int size
    );
}
