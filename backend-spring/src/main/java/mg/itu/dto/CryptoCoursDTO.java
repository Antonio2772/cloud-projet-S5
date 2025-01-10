 package mg.itu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CryptoCoursDTO {
    private Long cryptoId;
    private String cryptoLabel;
    private BigDecimal currentPrice;
    private LocalDateTime lastUpdate;
    private BigDecimal priceChange;  // Latest price change
    private BigDecimal percentageChange;  // Latest percentage change

    // Constructors
    public CryptoCoursDTO() {}

    public CryptoCoursDTO(Long cryptoId, String cryptoLabel, BigDecimal currentPrice, LocalDateTime lastUpdate) {
        this.cryptoLabel = cryptoLabel;
        this.currentPrice = currentPrice;
        this.lastUpdate = lastUpdate;
        this.cryptoId = cryptoId;
    }

    // Getters and Setters
    public String getCryptoLabel() { return cryptoLabel; }
    public void setCryptoLabel(String cryptoLabel) { this.cryptoLabel = cryptoLabel; }
    
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
    
    public BigDecimal getPriceChange() { return priceChange; }
    public void setPriceChange(BigDecimal priceChange) { this.priceChange = priceChange; }
    
    public BigDecimal getPercentageChange() { return percentageChange; }
    public void setPercentageChange(BigDecimal percentageChange) { this.percentageChange = percentageChange; }

    public Long getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(Long cryptoId) {
        this.cryptoId = cryptoId;
    }
}