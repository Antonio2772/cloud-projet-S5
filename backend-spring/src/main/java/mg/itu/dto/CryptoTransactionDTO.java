package mg.itu.dto;

import java.time.LocalDateTime;

public class CryptoTransactionDTO {
    
    private Long userId;
    private String cryptoLabel;
    private Double quantity;
    private Boolean isSale;
    private Boolean isPurchase;
    private LocalDateTime dateTransaction;

    public CryptoTransactionDTO(Long userId, String cryptoLabel, Double quantity, Boolean isSale, Boolean isPurchase, LocalDateTime dateTransaction) 
    {
        this.userId = userId;
        this.cryptoLabel = cryptoLabel;
        this.quantity = quantity;
        this.isSale = isSale;
        this.isPurchase = isPurchase;
        this.dateTransaction = dateTransaction;
    }    

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCryptoLabel() {
        return cryptoLabel;
    }

    public void setCryptoLabel(String cryptoLabel) {
        this.cryptoLabel = cryptoLabel;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Boolean getIsSale() {
        return isSale;
    }

    public void setIsSale(Boolean isSale) {
        this.isSale = isSale;
    }

    public Boolean getIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(Boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }
}