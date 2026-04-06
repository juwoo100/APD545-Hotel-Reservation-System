package ca.seneca.application.repository;

import ca.seneca.application.model.LoyaltyAccount;
import jakarta.persistence.EntityManager;

public class LoyaltyAccountRepository {
    public LoyaltyAccount findByGuestId(EntityManager em, Integer guestId) {
        return em.createQuery("""
                SELECT la FROM LoyaltyAccount la
                WHERE la.guest.guestId = :guestId
                """, LoyaltyAccount.class)
                .setParameter("guestId", guestId)
                .getResultStream().findFirst().orElse(null);
    }
    public LoyaltyAccount save(EntityManager em, LoyaltyAccount account) {
        if (account.getLoyaltyAccountId() == null) {
            em.persist(account);
            return account;
        }
        return em.merge(account);
    }
    public LoyaltyAccount update(EntityManager em, LoyaltyAccount account) {
        return em.merge(account);
    }
}
