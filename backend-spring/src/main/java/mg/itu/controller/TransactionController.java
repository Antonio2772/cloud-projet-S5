package mg.itu.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import mg.itu.service.TransactionService;
import mg.itu.model.Transaction;
import mg.itu.dto.ApiResponse;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")  
public class TransactionController {

    @Autowired
    private TransactionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(@RequestBody Transaction transaction) {
        Transaction savedTransaction = service.saveTransaction(transaction);
        ApiResponse<Transaction> response = new ApiResponse<>(
            "success",
            "Transaction created successfully. Please check your email to validate the transaction.",
            savedTransaction
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Transaction>> validateTransaction(
            @RequestParam String token,
            @RequestParam Long transactionId) {
        Transaction validatedTransaction = service.validateTransaction(token, transactionId);
        ApiResponse<Transaction> response = new ApiResponse<>(
            "success",
            "Transaction validated successfully",
            validatedTransaction
        );
        
        return ResponseEntity.ok(response);
    }
}