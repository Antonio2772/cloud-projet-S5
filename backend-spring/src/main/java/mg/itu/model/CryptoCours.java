package mg.itu.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "crypto_cours")
public class CryptoCours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_crypto", nullable = false)
    private Crypto crypto;

    @Column(name = "cours", nullable = false)
    private BigDecimal cours;

    @Column(name = "date_cours", nullable = false)
    private LocalDateTime dateCours;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Crypto getCrypto() { return crypto; }
    public void setCrypto(Crypto crypto) { this.crypto = crypto; }
    
    public BigDecimal getCours() { return cours; }
    public void setCours(BigDecimal cours) { this.cours = cours; }
    
    public LocalDateTime getDateCours() { return dateCours; }
    public void setDateCours(LocalDateTime dateCours) { this.dateCours = dateCours; }
}