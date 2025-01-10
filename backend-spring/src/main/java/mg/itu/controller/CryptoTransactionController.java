package mg.itu.controller;

import mg.itu.dto.ApiResponse;
import mg.itu.dto.CryptoCoursDTO;
import mg.itu.dto.CryptoTransactionDTO;
import mg.itu.model.Crypto;
import mg.itu.model.CryptoTransaction;
import mg.itu.service.CryptoCoursService;
import mg.itu.service.CryptoPurchaseSaleService;
import mg.itu.service.CryptoTransactionService;
import mg.itu.service.CryptoService;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crypto-transactions")
@CrossOrigin(origins = "*")  
public class CryptoTransactionController {

    @Autowired
    private CryptoPurchaseSaleService cryptoPurchaseSaleService; 

    @Autowired
    private CryptoTransactionService transactionService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private CryptoCoursService cryptoCoursService;

    @PostMapping
    public ResponseEntity<?> handleCryptoTransaction(@RequestBody CryptoTransaction transaction) {
        if (transaction.getCrypto() == null || transaction.getCrypto().getId() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    "error",
                    "Crypto ID must be provided",
                    null
            ));
        }

        // Fetch the Crypto entity
        Crypto crypto = cryptoPurchaseSaleService.findById(transaction.getCrypto().getId());
        if (crypto == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    "error",
                    "Invalid Crypto ID",
                    null
            ));
        }
        transaction.setCrypto(crypto);

        // Check user balance
        Double userSolde = transactionService.getUserSolde(transaction.getUserId());
        BigDecimal currentCryptoCour = null;
        List<CryptoCoursDTO> cours = cryptoCoursService.getAllCurrentPrices();
        
        for (CryptoCoursDTO cryptoCoursDTO : cours) {
            if (cryptoCoursDTO.getCryptoId() == transaction.getCrypto().getId())
            { currentCryptoCour = cryptoCoursDTO.getCurrentPrice(); }
        }

        if (userSolde == null || currentCryptoCour == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    "error",
                    "User not found or no transactions available",
                    null
            ));
        }

        if (transaction.getIsSale() && transaction.getQuantity() * currentCryptoCour.doubleValue() > userSolde) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    "error",
                    "Insufficient balance for this transaction",
                    null
            ));
        }

        // Save the transaction
        CryptoTransaction savedTransaction = transactionService.saveTransaction(transaction);
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Crypto transaction processed successfully",
                savedTransaction
        ));
    }

    @GetMapping("/all")
    public List<CryptoTransactionDTO> getAllTransactions() {
        return cryptoPurchaseSaleService.getAllTransactions();
    }

    @GetMapping("/cryptos")
    public List<Crypto> getAllCryptos() {
        return cryptoService.getAllCryptos();
    }
}