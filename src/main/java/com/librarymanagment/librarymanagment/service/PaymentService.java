package com.librarymanagment.librarymanagment.service;

import com.librarymanagment.librarymanagment.dto.PaymentDto;
import com.librarymanagment.librarymanagment.dto.request.PaymentInitiateRequest;
import com.librarymanagment.librarymanagment.dto.request.PaymentVerifyRequest;
import com.librarymanagment.librarymanagment.dto.response.PaymentInitiateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

public interface PaymentService {
    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request);

    PaymentDto verifyPayment(PaymentVerifyRequest request);

    Page<PaymentDto> getAllPayments(Pageable pageable);




}
