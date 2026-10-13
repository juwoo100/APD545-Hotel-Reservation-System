package ca.seneca.application.service;

import ca.seneca.application.model.Reservation;
import ca.seneca.application.model.enums.LoyaltyStatus;
import ca.seneca.application.model.Guest;
import ca.seneca.application.model.LoyaltyAccount;
import ca.seneca.application.model.LoyaltyTransaction;
import ca.seneca.application.model.enums.LoyaltyTransactionType;
import ca.seneca.application.repository.LoyaltyAccountRepository;
import ca.seneca.application.repository.LoyaltyTransactionRepository;
import ca.seneca.application.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class LoyaltyService {
    private static final double DEFAULT_EARNING_RATE = 0.10;
    private static final double REDEMPTION_VALUE_PER_POINT = 0.10;
    private static final int MAX_REDEEMABLE_POINTS_PER_RESERVATION = 500;

    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;

    public LoyaltyService(LoyaltyAccountRepository loyaltyAccountRepository, LoyaltyTransactionRepository loyaltyTransactionRepository) {
        this.loyaltyAccountRepository = loyaltyAccountRepository;
        this.loyaltyTransactionRepository = loyaltyTransactionRepository;
    }

    public LoyaltyAccount findOrCreateLoyaltyAccount(Guest guest) {
        if (guest == null || guest.getGuestId() == null) {
            throw new IllegalArgumentException("Valid guest is required.");
        }

        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Guest managedGuest = em.find(Guest.class, guest.getGuestId());
            if (managedGuest == null) {
                throw new IllegalArgumentException("Guest not found.");
            }

            LoyaltyAccount account = loyaltyAccountRepository.findByGuestId(em, managedGuest.getGuestId());

            if (account == null) {
                account = new LoyaltyAccount();
                account.setGuest(managedGuest);
                account.setLoyaltyNumber(generateLoyaltyNumber());
                account.setPointsBalance(0);
                account.setEarningRate(DEFAULT_EARNING_RATE);
                account.setStatus(LoyaltyStatus.ACTIVE);
                account.setEnrolledAt(LocalDateTime.now());

                loyaltyAccountRepository.save(em, account);
            }

            em.getTransaction().commit();
            return account;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }finally {
            em.close();
        }
    }

    public int earnPoints(Guest guest, double paidAmount, String note) {
        if (guest == null || guest.getGuestId() == null) {
            throw new IllegalArgumentException("Valid guest is required.");
        }
        if (paidAmount <= 0) {
            throw new IllegalArgumentException("Paid amount must be greater than 0.");
        }

        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Guest managedGuest = em.find(Guest.class, guest.getGuestId());
            if (managedGuest == null) {
                throw new IllegalArgumentException("Guest not found.");
            }

            LoyaltyAccount account = loyaltyAccountRepository.findByGuestId(em, managedGuest.getGuestId());

            if (account == null) {
                account = new LoyaltyAccount();
                account.setGuest(managedGuest);
                account.setLoyaltyNumber(generateLoyaltyNumber());
                account.setPointsBalance(0);
                account.setEarningRate(DEFAULT_EARNING_RATE);
                account.setStatus(LoyaltyStatus.ACTIVE);
                account.setEnrolledAt(LocalDateTime.now());

                loyaltyAccountRepository.save(em, account);
            }

            double earningRate = account.getEarningRate() == null ? DEFAULT_EARNING_RATE : account.getEarningRate();
            int earnedPoints = (int) Math.floor(paidAmount * earningRate);
            int updatedBalance = safePoints(account.getPointsBalance()) + earnedPoints;

            account.setPointsBalance(updatedBalance);
            loyaltyAccountRepository.update(em, account);

            LoyaltyTransaction transaction = new LoyaltyTransaction();
            transaction.setLoyaltyAccount(account);
            transaction.setTransactionType(LoyaltyTransactionType.EARN);
            transaction.setPoints(earnedPoints);
            transaction.setAmountValue(paidAmount);
            transaction.setTransactionDate(LocalDate.now());
            transaction.setNote(note);

            loyaltyTransactionRepository.save(em, transaction);

            em.getTransaction().commit();
            return earnedPoints;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public double redeemPoints(Guest guest, int pointsToRedeem, String note) {
        if (guest == null || guest.getGuestId() == null) {
            throw new IllegalArgumentException("Valid guest is required.");
        }

        if (pointsToRedeem <= 0) {
            throw new IllegalArgumentException("Redeem amount must be greater than 0.");
        }

        if (pointsToRedeem > MAX_REDEEMABLE_POINTS_PER_RESERVATION) {
            throw new IllegalArgumentException("Points exceed per-reservation limit");
        }

        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Guest managedGuest = em.find(Guest.class, guest.getGuestId());
            if (managedGuest == null) {
                throw new IllegalArgumentException("Guest not found.");
            }

            LoyaltyAccount account = loyaltyAccountRepository.findByGuestId(em, managedGuest.getGuestId());

            if (account == null) {
                throw new IllegalArgumentException("Guest does not have a loyalty account.");
            }
            if (account.getStatus() != LoyaltyStatus.ACTIVE) {
                throw new IllegalArgumentException("Loyalty account is not active.");
            }

            int currentBalance = safePoints(account.getPointsBalance());
            if (pointsToRedeem > currentBalance) {
                throw new IllegalArgumentException("Not enough loyalty points to redeem.");
            }

            double redemptionAmount = pointsToRedeem * REDEMPTION_VALUE_PER_POINT;
            int updatedBalance = currentBalance - pointsToRedeem;

            account.setPointsBalance(updatedBalance);
            loyaltyAccountRepository.update(em, account);

            LoyaltyTransaction transaction = new LoyaltyTransaction();
            transaction.setLoyaltyAccount(account);
            transaction.setTransactionType(LoyaltyTransactionType.REDEEM);
            transaction.setPoints(pointsToRedeem);
            transaction.setAmountValue(redemptionAmount);
            transaction.setTransactionDate(LocalDate.now());
            transaction.setNote(note);

            loyaltyTransactionRepository.save(em, transaction);

            em.getTransaction().commit();
            return redemptionAmount;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public double redeemPointsForReservation(Reservation reservation, int pointsToRedeem, String note) {
        if (reservation == null || reservation.getReservationId() == null) {
            throw new IllegalArgumentException("Valid reservation is required.");
        }

        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Reservation managedReservation = em.find(Reservation.class, reservation.getReservationId());
            if (managedReservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            Guest guest = managedReservation.getGuest();
            if (guest == null || guest.getGuestId() == null) {
                throw new IllegalArgumentException("Reservation guest is invalid.");
            }

            LoyaltyAccount account = loyaltyAccountRepository.findByGuestId(em, guest.getGuestId());
            if (account == null) {
                throw new IllegalArgumentException("Guest does not have a loyalty account.");
            }
            if (account.getStatus() != LoyaltyStatus.ACTIVE) {
                throw new IllegalArgumentException("Loyalty account is not active.");
            }
            if (pointsToRedeem <= 0) {
                throw new IllegalArgumentException("Redeem amount must be greater than 0.");
            }
            if (pointsToRedeem > MAX_REDEEMABLE_POINTS_PER_RESERVATION) {
                throw new IllegalArgumentException("Points exceed per-reservation limit.");
            }

            int currentBalance = safePoints(account.getPointsBalance());
            if (pointsToRedeem > currentBalance) {
                throw new IllegalArgumentException("Not enough loyalty points to redeem.");
            }

            double redemptionAmount = pointsToRedeem * REDEMPTION_VALUE_PER_POINT;
            double currentTotal = managedReservation.getTotalAmount() == null ? 0.0 : managedReservation.getTotalAmount();

            if (redemptionAmount > currentTotal) {
                redemptionAmount = currentTotal;
                pointsToRedeem = (int) Math.floor(currentTotal / REDEMPTION_VALUE_PER_POINT);
            }

            account.setPointsBalance(currentBalance - pointsToRedeem);
            loyaltyAccountRepository.update(em, account);

            LoyaltyTransaction transaction = new LoyaltyTransaction();
            transaction.setLoyaltyAccount(account);
            transaction.setTransactionType(LoyaltyTransactionType.REDEEM);
            transaction.setPoints(pointsToRedeem);
            transaction.setAmountValue(redemptionAmount);
            transaction.setTransactionDate(LocalDate.now());
            transaction.setNote(note);

            loyaltyTransactionRepository.save(em, transaction);

            managedReservation.setUsedPoints(pointsToRedeem);
            managedReservation.setDiscountAmount(
                    (managedReservation.getDiscountAmount() == null ? 0.0 : managedReservation.getDiscountAmount()) + redemptionAmount
            );
            managedReservation.setTotalAmount(currentTotal - redemptionAmount);

            em.merge(managedReservation);

            em.getTransaction().commit();
            return redemptionAmount;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public int earnPointsFromReservation(Reservation reservation, double paidAmount, String note) {
        if (reservation == null || reservation.getReservationId() == null) {
            throw new IllegalArgumentException("Valid reservation is required.");
        }
        if (paidAmount <= 0) {
            throw new IllegalArgumentException("Paid amount must be greater than 0.");
        }

        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Reservation managedReservation = em.find(Reservation.class, reservation.getReservationId());
            if (managedReservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            Guest guest = managedReservation.getGuest();
            if (guest == null || guest.getGuestId() == null) {
                throw new IllegalArgumentException("Reservation guest is invalid.");
            }

            LoyaltyAccount account = loyaltyAccountRepository.findByGuestId(em, guest.getGuestId());

            if (account == null) {
                account = new LoyaltyAccount();
                account.setGuest(guest);
                account.setLoyaltyNumber(generateLoyaltyNumber());
                account.setPointsBalance(0);
                account.setEarningRate(DEFAULT_EARNING_RATE);
                account.setStatus(LoyaltyStatus.ACTIVE);
                account.setEnrolledAt(LocalDateTime.now());
                loyaltyAccountRepository.save(em, account);
            }

            double earningRate = account.getEarningRate() == null ? DEFAULT_EARNING_RATE : account.getEarningRate();
            int earnedPoints = (int) Math.floor(paidAmount * earningRate);

            account.setPointsBalance(safePoints(account.getPointsBalance()) + earnedPoints);
            loyaltyAccountRepository.update(em, account);

            LoyaltyTransaction transaction = new LoyaltyTransaction();
            transaction.setLoyaltyAccount(account);
            transaction.setTransactionType(LoyaltyTransactionType.EARN);
            transaction.setPoints(earnedPoints);
            transaction.setAmountValue(paidAmount);
            transaction.setTransactionDate(LocalDate.now());
            transaction.setNote(note);

            loyaltyTransactionRepository.save(em, transaction);

            managedReservation.setEarnedPoints(
                    (managedReservation.getEarnedPoints() == null ? 0 : managedReservation.getEarnedPoints()) + earnedPoints
            );
            em.merge(managedReservation);

            em.getTransaction().commit();
            return earnedPoints;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private int safePoints(Integer pointsBalance) {
        return pointsBalance == null ? 0 : pointsBalance;
    }

    private String generateLoyaltyNumber() {
        return "LOY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
