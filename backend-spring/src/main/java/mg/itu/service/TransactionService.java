package mg.itu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

import mg.itu.model.Transaction;
import mg.itu.model.User;
import mg.itu.repository.TransactionRepository;
import mg.itu.repository.UserRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;

    public Transaction saveTransaction(Transaction transaction) {
        // Generate validation token
        String validationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiration = LocalDateTime.now().plusHours(1);   // Expiration after one hour

        transaction.setValidationToken(validationToken);
        transaction.setValidationTokenExpiresAt(tokenExpiration);
        transaction.setValidated(false);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Send validation email
        User user = userRepository.findById(transaction.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        emailService.sendTransactionValidationEmail(
            user.getUserEmail(),
            user.getUserName(),
            validationToken,
            savedTransaction.getId()
        );

        return savedTransaction;
    }

    public Transaction validateTransaction(String token, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndValidationToken(transactionId, token)
            .orElseThrow(() -> new RuntimeException("Invalid transaction or token"));

        if (LocalDateTime.now().isAfter(transaction.getValidationTokenExpiresAt())) {
            throw new RuntimeException("Validation token has expired");
        }

        if (transaction.isValidated()) {
            throw new RuntimeException("Transaction is already validated");
        }

        transaction.setValidated(true);
        transaction.setValidatedAt(LocalDateTime.now());
        
        return transactionRepository.save(transaction);
    }
}