package mg.itu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import mg.itu.repository.CryptoRepository;

import java.util.List;
import mg.itu.model.Crypto;

@Service
public class CryptoService {
    
    @Autowired
    CryptoRepository cryptoRepository;

    @Transactional(readOnly = true)
    public List<Crypto> getAllCryptos() {
        return cryptoRepository.findAll();
    }
}