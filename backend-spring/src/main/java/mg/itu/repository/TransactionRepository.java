package mg.itu.repository;

import mg.itu.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.id = :transactionId AND t.validationToken = :token")
    Optional<Transaction> findByIdAndValidationToken(
        @Param("transactionId") Long transactionId, 
        @Param("token") String token
    );
}