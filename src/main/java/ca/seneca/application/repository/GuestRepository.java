package ca.seneca.application.repository;

import ca.seneca.application.db.Database;
import ca.seneca.application.model.Guest;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class GuestRepository {
    private final Database db = Database.getInstance();

    public Guest save(EntityManager em, Guest guest) {
        if (guest.getGuestId() == null) {
            em.persist(guest);
            return guest;
        }
        return em.merge(guest);
    }

    public Guest save(Guest guest) {
        EntityManager em = db.createEntityManager();
        try {
            em.getTransaction().begin();
            Guest result;
            if (guest.getGuestId() == null) {
                em.persist(guest);
                result = guest;
            } else {
                result = em.merge(guest);
            }
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<Guest> findById(Integer guestId) {
        EntityManager em = db.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Guest.class, guestId));
        } finally {
            em.close();
        }
    }

    public Optional<Guest> findByPhone(String phone) {
        EntityManager em = db.createEntityManager();
        try {
            List<Guest> result = em.createQuery(
                    "SELECT g FROM Guest g WHERE g.phone = :phone",
                    Guest.class
            ).setParameter("phone", phone).getResultList();

            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        } finally {
            em.close();
        }
    }

    public List<Guest> findAll() {
        EntityManager em = db.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT g FROM Guest g ORDER BY g.lastName, g.firstName",
                    Guest.class
            ).getResultList();
        } finally {
            em.close();
        }
    }
}
