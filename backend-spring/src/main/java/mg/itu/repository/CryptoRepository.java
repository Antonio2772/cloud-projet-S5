package mg.itu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mg.itu.model.Crypto;

public interface CryptoRepository extends JpaRepository<Crypto, Long> 
{ }