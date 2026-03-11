package com.librarymanagment.librarymanagment.mapper;

import com.librarymanagment.librarymanagment.dto.PaymentDto;
import com.librarymanagment.librarymanagment.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    // ─── Entity → DTO ────────────────────────────────────────────────────────

    public PaymentDto toDto(Payment payment) {
        if (payment == null) return null;

        return PaymentDto.builder()
                .id(payment.getId())
                .userId(payment.getUser() != null ? payment.getUser().getId() : null)
                .userName(payment.getUser() != null ? payment.getUser().getFullName() : null)
                .userEmail(payment.getUser() != null ? payment.getUser().getEmail() : null)
                .subscriptionId(payment.getSubscription() != null ? payment.getSubscription().getId() : null)
                .paymentType(payment.getPaymentType())
                .paymentStatus(payment.getPaymentStatus())
                .gateway(payment.getGateway())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .gatewayPaymentId(payment.getGatewayPaymentId())
                .gatewayOrderId(payment.getGatewayOrderId())
                .gatewaySignature(payment.getGatewaySignature())
                .paymentMethod(payment.getPaymentMethod())
                .description(payment.getDescription())
                .failureReason(payment.getFailureReason())
                .initiatedAt(payment.getInitiatedAt())
                .completedAt(payment.getCompletedAt())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    // ─── DTO List → DTO List ─────────────────────────────────────────────────

    public List<PaymentDto> toDtoList(List<Payment> payments) {
        if (payments == null) return List.of();
        return payments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}


