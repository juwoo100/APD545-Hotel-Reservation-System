package ca.seneca.application.model;

import ca.seneca.application.model.enums.LoyaltyStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LoyaltyAccount")
public class LoyaltyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loyalty_account_id")
    private Integer loyaltyAccountId;

    @OneToOne
    @JoinColumn(name = "guest_id", unique = true, nullable = false)
    private Guest guest;

    @Column(name = "loyalty_number")
    private String loyaltyNumber;

    @Column(name = "points_balance")
    private Integer pointsBalance = 0;

    @Column(name = "earning_rate")
    private Double earningRate = 0.10;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoyaltyStatus status;

    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;

    @OneToMany(mappedBy = "loyaltyAccount")
    private List<LoyaltyTransaction> loyaltyTransactions = new ArrayList<>();

    public LoyaltyAccount() {
    }

    public Integer getLoyaltyAccountId() {
        return loyaltyAccountId;
    }

    public void setLoyaltyAccountId(Integer loyaltyAccountId) {
        this.loyaltyAccountId = loyaltyAccountId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public String getLoyaltyNumber() {
        return loyaltyNumber;
    }

    public void setLoyaltyNumber(String loyaltyNumber) {
        this.loyaltyNumber = loyaltyNumber;
    }

    public Integer getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(Integer pointsBalance) {
        this.pointsBalance = pointsBalance;
    }

    public Double getEarningRate() {
        return earningRate;
    }

    public void setEarningRate(Double earningRate) {
        this.earningRate = earningRate;
    }

    public LoyaltyStatus getStatus() {
        return status;
    }

    public void setStatus(LoyaltyStatus status) {
        this.status = status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public List<LoyaltyTransaction> getLoyaltyTransactions() {
        return loyaltyTransactions;
    }

    public void setLoyaltyTransactions(List<LoyaltyTransaction> loyaltyTransactions) {
        this.loyaltyTransactions = loyaltyTransactions;
    }
}