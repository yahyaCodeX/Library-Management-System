package com.librarymanagment.librarymanagment.controller;

import com.librarymanagment.librarymanagment.constant.FineStatus;
import com.librarymanagment.librarymanagment.constant.FineType;
import com.librarymanagment.librarymanagment.dto.FineDto;
import com.librarymanagment.librarymanagment.dto.request.WaiveFineRequest;
import com.librarymanagment.librarymanagment.dto.request.createFineRequest;
import com.librarymanagment.librarymanagment.dto.response.PageResponse;
import com.librarymanagment.librarymanagment.dto.response.PaymentInitiateResponse;
import com.librarymanagment.librarymanagment.service.Implementations.FineServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fines")
public class FineController {
    private final FineServiceImpl fineService;

    @PostMapping("/create-fine")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FineDto> createFine(@Valid @RequestBody createFineRequest request){
        FineDto dto=fineService.createFine(request);
        return  ResponseEntity.ok(dto);
    }

    @PostMapping("/{fineId}/pay")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentInitiateResponse> payFine(@PathVariable Long fineId,@RequestParam(required = false) String transactionId){
        PaymentInitiateResponse response=fineService.payFine(fineId,transactionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{fineId}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> markFineAsPaid(@Valid @PathVariable Long fineId,@RequestBody Long amount,String transactionID){
        fineService.markFineAsPaid(fineId,amount,transactionID);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/waive-fine")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FineDto> WaiveFine(@Valid @RequestBody WaiveFineRequest request){
        FineDto dto=fineService.waiveFine(request);
        return ResponseEntity.ok(dto);
    }

     @GetMapping("/my-fines")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FineDto>> getMyFines(@RequestParam(required = false) FineStatus status,@RequestParam(required = false) FineType type){
         List<FineDto> myFines = fineService.getMyFines(status, type);
            return ResponseEntity.ok(myFines);
     }

     @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<FineDto>> getAllFines(
                @RequestParam(required = false) FineStatus status,
                @RequestParam(required = false) FineType type,
                @RequestParam(required = false) Long userId,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size
        ){
            PageResponse<FineDto> response=fineService.getAllFines(status,type,userId,page,size);
            return ResponseEntity.ok(response);
        }

}
