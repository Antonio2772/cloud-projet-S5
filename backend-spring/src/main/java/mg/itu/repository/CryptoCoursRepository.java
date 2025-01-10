package mg.itu.repository;

import mg.itu.model.CryptoCours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface CryptoCoursRepository extends JpaRepository<CryptoCours, Long> {

       @Query(value = "SELECT * FROM crypto_cours " +
              "WHERE id_crypto = :cryptoId " +
              "ORDER BY date_cours DESC LIMIT 2", 
              nativeQuery = true)
       List<CryptoCours> findLatestTwoByCryptoId(@Param("cryptoId") Long cryptoId);

       @Query(value = "SELECT * FROM crypto_cours " +
              "WHERE id_crypto = :cryptoId " +
              "AND date_cours >= :startDate " +
              "ORDER BY date_cours ASC", 
              nativeQuery = true)
       List<CryptoCours> findPriceHistory(
              @Param("cryptoId") Long cryptoId,
              @Param("startDate") LocalDateTime startDate
       );

       @Query(value = "SELECT * FROM crypto_cours " +
              "WHERE id_crypto = :cryptoId " +
              "ORDER BY date_cours DESC LIMIT 1", 
              nativeQuery = true)
       CryptoCours findTopByCryptoOrderByDateCoursDesc(@Param("cryptoId") Long cryptoId);
} 