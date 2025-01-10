package mg.itu.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_user", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double deposit;

    @Column(nullable = false)
    private Double withdrawal;

    @Column(name = "date_transaction", nullable = false)
    private LocalDateTime dateTransaction;

    @Column(name = "validation_token")
    private String validationToken;

    @Column(name = "validation_token_expires_at")
    private LocalDateTime validationTokenExpiresAt;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "is_validated")
    private boolean isValidated = false;

    // Getters and Setters
    public Long getId() 
    { return id; }

    public void setId(Long id) 
    { this.id = id; }

    public Long getUserId() 
    { return userId; }

    public void setUserId(Long userId) 
    { this.userId = userId; }

    public Double getDeposit() 
    { return deposit; }

    public void setDeposit(Double deposit) 
    { this.deposit = deposit; }

    public Double getWithdrawal() 
    { return withdrawal; }

    public void setWithdrawal(Double withdrawal) 
    { this.withdrawal = withdrawal; }

    public LocalDateTime getDateTransaction() 
    { return dateTransaction; }

    public void setDateTransaction(LocalDateTime dateTransaction) 
    { this.dateTransaction = dateTransaction; }

    public String getValidationToken() {
        return validationToken;
    }

    public void setValidationToken(String validationToken) {
        this.validationToken = validationToken;
    }

    public LocalDateTime getValidationTokenExpiresAt() {
        return validationTokenExpiresAt;
    }

    public void setValidationTokenExpiresAt(LocalDateTime validationTokenExpiresAt) {
        this.validationTokenExpiresAt = validationTokenExpiresAt;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public void setValidated(boolean validated) {
        isValidated = validated;
    }
}