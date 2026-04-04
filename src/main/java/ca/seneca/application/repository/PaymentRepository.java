package ca.seneca.application.repository;

import ca.seneca.application.model.Payment;
import jakarta.persistence.EntityManager;

public class PaymentRepository {

    public Payment save(EntityManager em, Payment payment) {
        if (payment.getPaymentId() == null) {
            em.persist(payment);
            return payment;
        }
        return em.merge(payment);
    }

    public Payment findById(EntityManager em, Integer id) {
        return em.find(Payment.class, id);
    }
}