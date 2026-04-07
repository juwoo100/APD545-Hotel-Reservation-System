package ca.seneca.application.model;

import ca.seneca.application.model.enums.LoyaltyTransactionType;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "LoyaltyTransaction")
public class LoyaltyTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loyalty_transaction_id")
    private Integer loyaltyTransactionId;

    @ManyToOne
    @JoinColumn(name = "loyalty_account_id", nullable = false)
    private LoyaltyAccount loyaltyAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private LoyaltyTransactionType transactionType;

    private Integer points;

    @Column(name = "amount_value")
    private Double amountValue;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    private String note;

    public LoyaltyTransaction() {
    }

    public Integer getLoyaltyTransactionId() {
        return loyaltyTransactionId;
    }

    public void setLoyaltyTransactionId(Integer loyaltyTransactionId) {
        this.loyaltyTransactionId = loyaltyTransactionId;
    }

    public LoyaltyAccount getLoyaltyAccount() {
        return loyaltyAccount;
    }

    public void setLoyaltyAccount(LoyaltyAccount loyaltyAccount) {
        this.loyaltyAccount = loyaltyAccount;
    }

    public LoyaltyTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(LoyaltyTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Double getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(Double amountValue) {
        this.amountValue = amountValue;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
