package mg.itu.repository;

import mg.itu.dto.CryptoTransactionDTO;
import mg.itu.model.CryptoTransaction;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoPurchaseSaleRepository extends CrudRepository<CryptoTransaction, Long> {

    @Query("SELECT new mg.itu.dto.CryptoTransactionDTO(ct.userId, c.label, ct.quantity, ct.isSale, ct.isPurchase, ct.dateTransaction) " +
       "FROM CryptoTransaction ct " +
       "JOIN Crypto c ON ct.crypto.id = c.id")
    List<CryptoTransactionDTO> findAllTransactions();
}