package ca.seneca.application.repository;

import ca.seneca.application.model.LoyaltyTransaction;
import jakarta.persistence.EntityManager;

import java.util.List;

public class LoyaltyTransactionRepository {
    public LoyaltyTransaction save(EntityManager em, LoyaltyTransaction transaction) {
        if (transaction.getLoyaltyTransactionId() == null) {
            em.persist(transaction);
            return transaction;
        }
        return em.merge(transaction);
    }

    public List<LoyaltyTransaction> findByAccountId(EntityManager em, Integer accountId) {
        return em.createQuery("""
                SELECT lt FROM LoyaltyTransaction lt
                WHERE lt.loyaltyAccount.loyaltyAccountId = :accountId
                ORDER BY lt.transactionDate DESC
                """, LoyaltyTransaction.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }
}
