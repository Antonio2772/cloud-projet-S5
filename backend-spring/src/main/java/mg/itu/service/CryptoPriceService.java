package mg.itu.service;

import mg.itu.model.Crypto;
import mg.itu.model.CryptoCours;
import mg.itu.repository.CryptoRepository;
import mg.itu.repository.CryptoCoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CryptoPriceService {

    @Autowired
    private CryptoRepository cryptoRepository;

    @Autowired
    private CryptoCoursRepository cryptoCoursRepository;

    private final Random random = new Random();
    private final AtomicBoolean isGenerating = new AtomicBoolean(false);

    private static final BigDecimal MIN_PRICE = BigDecimal.valueOf(1000);
    private static final BigDecimal MAX_PRICE = BigDecimal.valueOf(100000);

    @PostConstruct
    public void init() {
        System.out.println("Init ...");
        startPriceGeneration();
    }

    @Scheduled(fixedRate = 10000) // Runs every 10 seconds
    @Transactional
    public void generateNewPrices() {
        if (!isGenerating.get()) {
            return;
        }

        List<Crypto> cryptos = cryptoRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Crypto crypto : cryptos) {
            BigDecimal currentPrice = getLatestPrice(crypto);

            // Generate a random percentage change between -10% and +10%
            BigDecimal changePercentage = BigDecimal.valueOf(
                (random.nextDouble() * 20 - 10) / 100 
            );

            BigDecimal priceChange = currentPrice.multiply(changePercentage);
            BigDecimal newPrice = currentPrice.add(priceChange)
                .setScale(2, RoundingMode.HALF_UP);

            // Ensure price stays within allowed range
            newPrice = newPrice.max(MIN_PRICE).min(MAX_PRICE);

            CryptoCours newCours = new CryptoCours();
            newCours.setCrypto(crypto);
            newCours.setCours(newPrice);
            newCours.setDateCours(now);

            cryptoCoursRepository.save(newCours);
        }
    }

    private BigDecimal getLatestPrice(Crypto crypto) {
        CryptoCours latestCours = cryptoCoursRepository.findTopByCryptoOrderByDateCoursDesc(crypto.getId());
        if (latestCours != null) {
            return latestCours.getCours();
        }
        
        // Generate a base price if no previous price exists
        return MIN_PRICE.add(
            BigDecimal.valueOf(random.nextDouble()).multiply(MAX_PRICE.subtract(MIN_PRICE))
        ).setScale(2, RoundingMode.HALF_UP);
    }

    public void startPriceGeneration() {
        isGenerating.set(true);
    }

    public void stopPriceGeneration() {
        isGenerating.set(false);
    }

    public boolean isGenerating() {
        return isGenerating.get();
    }
}