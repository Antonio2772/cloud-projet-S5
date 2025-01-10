package mg.itu.model;


import jakarta.persistence.*;

@Entity
@Table(name = "crypto")
public class Crypto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;

    // Getters and Setters
    public Long getId() 
    { return id; }

    public void setId(Long id) 
    { this.id = id; }

    public String getLabel() 
    { return label; }

    public void setLabel(String label) 
    { this.label = label; }
}