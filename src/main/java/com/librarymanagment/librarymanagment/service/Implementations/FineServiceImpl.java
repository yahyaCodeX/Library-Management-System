package com.librarymanagment.librarymanagment.service.Implementations;

import com.librarymanagment.librarymanagment.constant.FineStatus;
import com.librarymanagment.librarymanagment.constant.FineType;
import com.librarymanagment.librarymanagment.constant.PaymentGateway;
import com.librarymanagment.librarymanagment.constant.PaymentType;
import com.librarymanagment.librarymanagment.dto.FineDto;
import com.librarymanagment.librarymanagment.dto.request.PaymentInitiateRequest;
import com.librarymanagment.librarymanagment.dto.request.WaiveFineRequest;
import com.librarymanagment.librarymanagment.dto.request.createFineRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.dto.response.PaymentInitiateResponse;
import com.librarymanagment.librarymanagment.entity.BookLoan;
import com.librarymanagment.librarymanagment.entity.Fine;
import com.librarymanagment.librarymanagment.entity.User;
import com.librarymanagment.librarymanagment.mapper.FineMapper;
import com.librarymanagment.librarymanagment.repository.BookLoanRepository;
import com.librarymanagment.librarymanagment.repository.FineRepository;
import com.librarymanagment.librarymanagment.service.FineService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {
    private final BookLoanRepository bookLoanRepository;
    private final FineRepository fineRepository;
    private final FineMapper fineMapper;
    private final UserServiceImpl userService;
    private final PaymentServiceImpl paymentService;
    @Override
    public FineDto createFine(createFineRequest request) {
        BookLoan bookLoan=bookLoanRepository.findById(request.getBookLoanId())
                .orElseThrow(()->new RuntimeException("Book loan not found with id: "+request.getBookLoanId()));

        Fine fine= Fine.builder()
                .user(bookLoan.getUser())
                .bookLoan(bookLoan)
                .fineType(request.getFineType())
                .amount(request.getAmount())
                .fineStatus(FineStatus.PENDING)
                .reason(request.getReason())
                .note(request.getNotes())
                .build();

        Fine savedFine=fineRepository.save(fine);
        return fineMapper.toDto(savedFine);
    }

    @Override
    public PaymentInitiateResponse payFine(Long fineId, String transactionId) {
        //1.validate fine exists
        Fine fine=fineRepository.findById(fineId)
                .orElseThrow(()->new RuntimeException("Fine not found with id: "+fineId));

        // 2.check already paid
        if(fine.getFineStatus().equals(FineStatus.PAID)){
            throw new RuntimeException("Fine with id: "+fineId+" is already paid");
        }
        if(fine.getFineStatus().equals(FineStatus.WAIVED)){
            throw new RuntimeException("Fine with id: "+fineId+" is waived ");
        }

        // 3. Initiate payment
        User user=userService.getCurrentUser();
        PaymentInitiateRequest request=PaymentInitiateRequest.builder()
                .userId(user.getId())
                .fineId(fine.getId())
                .paymentType(PaymentType.FINE)
                .gateway(PaymentGateway.STRIPE)
                .amount(fine.getAmount())
                .description("Library Fine Payment")
                .build();
        return paymentService.initiatePayment(request);
    }

    @Override
    public void markFineAsPaid(Long fineId, Long amount, String transactionId) {
        Fine fine=fineRepository.findById(fineId)
                .orElseThrow(()->new RuntimeException("Fine not found with id: "+fineId));

        //Apply payment amount to fine
        fine.applyPayment(amount);
        fine.setTransactionId(transactionId);
        fine.setFineStatus(FineStatus.PAID);
        fine.setUpdatedAt(LocalDateTime.now());

        fineRepository.save(fine);

    }

    @Override
    public FineDto waiveFine(WaiveFineRequest request) {
        Fine fine=fineRepository.findById(request.getFineId())
                .orElseThrow(()->new RuntimeException("Fine not found with id: "+request.getFineId()));

        //2. check if already paid or waived
        if(fine.getFineStatus().equals(FineStatus.PAID)){
            throw new RuntimeException("Fine is already paid and Cannot be waived ");
        }
        if(fine.getFineStatus().equals(FineStatus.WAIVED)){
            throw new RuntimeException("Fine is already waived ");
        }

        // 3.Waive the Fine
        User currentAdmin=userService.getCurrentUser();
        fine.waive(currentAdmin,request.getReason());

        // 4.save and return
        Fine savedFine=fineRepository.save(fine);
        log.info("Fine {} Waived by admin {}",fine.getId(),currentAdmin.getId());

        return fineMapper.toDto(savedFine);
    }

    @Override
    public List<FineDto> getMyFines(FineStatus status, FineType type) {
        User currentUser=userService.getCurrentUser();
        List<Fine> fines;

        //Apply filters based on status and type
        if(status !=null && type!=null){
            //Both filters applied
            fines=fineRepository.findByUserId(currentUser.getId()).stream()
                    .filter((f->f.getFineStatus()==status && f.getFineType()==type))
                    .collect(Collectors.toList());
        }else if(status!=null){
            //Filter by status only
            fines=fineRepository.findByUserId(currentUser.getId()).stream()
                    .filter(f->f.getFineStatus()==status)
                    .collect(Collectors.toList());
        } else if (type!=null) {
            //Filter by type only
            fines=fineRepository.findByUserIdAndFineType(currentUser.getId(),type);
        }else{
            //no filter all fines for user
            fines=fineRepository.findByUserId(currentUser.getId());
        }
        return fines.stream()
                .map(fineMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public PageResponse<FineDto> getAllFines(FineStatus status, FineType type, Long userId, int page, int size) {
        Pageable pageable= PageRequest.of(page,size, Sort.by("createdAt").descending());

        Page<Fine> finePage=fineRepository.findAllWithFilters(
                userId,
                status,
                type,
                pageable
        );
        return convertToPageResponse(finePage);
    }
    private PageResponse<FineDto> convertToPageResponse(Page<Fine> finePage) {
        List<FineDto> fineDtos=finePage.getContent().stream()
                .map(fineMapper::toDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                fineDtos,
                finePage.getNumber(),
                finePage.getSize(),
                finePage.getTotalElements(),
                finePage.getTotalPages(),
                finePage.isLast(),
                finePage.isFirst(),
                finePage.isEmpty()
        );
    }
}
