package ca.seneca.application.service;

import ca.seneca.application.model.Admin;
import ca.seneca.application.model.Payment;
import ca.seneca.application.model.Reservation;
import ca.seneca.application.model.enums.PaymentMethod;
import ca.seneca.application.model.enums.PaymentStatus;
import ca.seneca.application.repository.PaymentRepository;
import ca.seneca.application.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(Reservation reservation, Admin admin, double amount, PaymentMethod method, String note) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation is required");
        }
        if (admin == null) {
            throw new IllegalArgumentException("Admin is required");
        }
        if (method == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (reservation.getTotalAmount() == null || reservation.getTotalAmount() <= 0) {
            throw new IllegalArgumentException("Reservation total amount is invalid");
        }

        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Reservation managedReservation = em.find(Reservation.class, reservation.getReservationId());
            if (managedReservation == null) {
                throw new IllegalArgumentException("Reservation not found");
            }

            Admin managedAdmin = em.find(Admin.class, admin.getAdminId());
            if (managedAdmin == null) {
                throw new IllegalArgumentException("Admin not found");
            }

            double alreadyPaid = 0.0;
            if (managedReservation.getPayments() != null) {
                for (Payment existingPayment : managedReservation.getPayments()) {
                    if (existingPayment.getAmount() != null) {
                        alreadyPaid += existingPayment.getAmount();
                    }
                }
            }

            double remainingBalance = managedReservation.getTotalAmount() - alreadyPaid;
            if (amount > remainingBalance) {
                throw new IllegalArgumentException("Payment amount exceeds remaining balance");
            }

            Payment payment = new Payment();
            payment.setReservation(managedReservation);
            payment.setAdmin(managedAdmin);
            payment.setPaymentDate(LocalDate.now());
            payment.setAmount(amount);
            payment.setPaymentMethod(method);
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setNote(note);

            managedReservation.addPayment(payment);
            paymentRepository.save(em, payment);

            double newPaidTotal = alreadyPaid + amount;

            if (newPaidTotal >= managedReservation.getTotalAmount()) {
                managedReservation.setPaymentStatus(PaymentStatus.PAID);
            } else {
                managedReservation.setPaymentStatus(PaymentStatus.PARTIAL);
            }

            em.merge(managedReservation);

            em.getTransaction().commit();
            return payment;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public double getPaidAmount(Reservation reservation) {
        if (reservation == null || reservation.getPayments() == null) {
            return 0.0;
        }

        double total = 0.0;
        for (Payment payment : reservation.getPayments()) {
            if (payment.getAmount() != null) {
                total += payment.getAmount();
            }
        }
        return total;
    }

    public double getRemainingBalance(Reservation reservation) {
        if (reservation == null || reservation.getTotalAmount() == null) {
            return 0.0;
        }
        return reservation.getTotalAmount() - getPaidAmount(reservation);
    }
}
