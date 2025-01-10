package mg.itu.service;

import mg.itu.dto.CryptoCoursDTO;
import mg.itu.model.Crypto;
import mg.itu.model.CryptoCours;

import mg.itu.repository.CryptoCoursRepository;
import mg.itu.repository.CryptoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CryptoCoursService {
    
    @Autowired
    private CryptoRepository cryptoRepository;
    
    @Autowired
    private CryptoCoursRepository cryptoCoursRepository;

    @Transactional(readOnly = true)
    public List<CryptoCoursDTO> getAllCurrentPrices() {
        List<CryptoCoursDTO> result = new ArrayList<>();
        List<Crypto> cryptos = cryptoRepository.findAll();
        
        for (Crypto crypto : cryptos) {
            List<CryptoCours> latestTwoCours = getLatestTwoCours(crypto.getId());
            if (!latestTwoCours.isEmpty()) {
                CryptoCours latestCours = latestTwoCours.get(0);
                CryptoCoursDTO dto = new CryptoCoursDTO(
                    latestCours.getCrypto().getId(),
                    crypto.getLabel(),
                    latestCours.getCours(),
                    latestCours.getDateCours()
                );

                if (latestTwoCours.size() > 1) {
                    CryptoCours previousCours = latestTwoCours.get(1);

                    // Calculate latest price change
                    BigDecimal priceChange = latestCours.getCours().subtract(previousCours.getCours());
                    BigDecimal percentageChange = priceChange
                        .divide(previousCours.getCours(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                    
                    dto.setPriceChange(priceChange);
                    dto.setPercentageChange(percentageChange);
                }

                result.add(dto);
            }
        }
        
        return result;
    }

    @Transactional(readOnly = true)
    public CryptoCoursDTO getCryptoPrice(Long cryptoId) {
        List<CryptoCours> latestTwoCours = getLatestTwoCours(cryptoId);
        if (latestTwoCours.isEmpty()) {
            return null;
        }

        CryptoCours latestCours = latestTwoCours.get(0);
        CryptoCoursDTO dto = new CryptoCoursDTO(
            latestCours.getCrypto().getId(),
            latestCours.getCrypto().getLabel(),
            latestCours.getCours(),
            latestCours.getDateCours()
        );

        if (latestTwoCours.size() > 1) {
            CryptoCours previousCours = latestTwoCours.get(1);

            // Calculate latest price change
            BigDecimal priceChange = latestCours.getCours().subtract(previousCours.getCours());
            BigDecimal percentageChange = priceChange
                .divide(previousCours.getCours(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            
            dto.setPriceChange(priceChange);
            dto.setPercentageChange(percentageChange);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public List<CryptoCoursDTO> getHistoricalPrices(Long cryptoId) {
        // Calculate the start date for the last 7 days
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        List<CryptoCours> historicalCours = cryptoCoursRepository.findPriceHistory(cryptoId, startDate);

        List<CryptoCoursDTO> result = new ArrayList<>();
        for (CryptoCours cours : historicalCours) {
            CryptoCoursDTO dto = new CryptoCoursDTO(
                cours.getCrypto().getId(),
                cours.getCrypto().getLabel(),
                cours.getCours(),
                cours.getDateCours()
            );
            result.add(dto);
        }

        return result;
    }

    private List<CryptoCours> getLatestTwoCours(Long cryptoId) {
        return cryptoCoursRepository.findLatestTwoByCryptoId(cryptoId);
    }
}