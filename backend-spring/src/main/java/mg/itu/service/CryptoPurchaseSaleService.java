package mg.itu.service;

import mg.itu.dto.CryptoTransactionDTO;
import mg.itu.model.Crypto;
import mg.itu.repository.CryptoPurchaseSaleRepository;
import mg.itu.repository.CryptoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CryptoPurchaseSaleService {

    @Autowired
    private CryptoPurchaseSaleRepository repository;

    @Autowired
    private CryptoRepository cryptoRepository;

    public List<CryptoTransactionDTO> getAllTransactions() {
        return repository.findAllTransactions();
    }

    public Crypto findById(Long id) {
        return cryptoRepository.findById(id).orElse(null);
    }
}