package mg.itu.repository;

import mg.itu.model.CryptoTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoTransactionRepository extends JpaRepository<CryptoTransaction, Long> 
{ }