package ca.seneca.application.repository;

import ca.seneca.application.model.Guest;
import jakarta.persistence.EntityManager;

public class GuestRepository {

    public Guest save(EntityManager em, Guest guest) {
        if (guest.getGuestId() == null) {
            em.persist(guest);
            return guest;
        }
        return em.merge(guest);

    }

    public Guest findById(EntityManager em, Integer id) {
        return em.find(Guest.class, id);
    }
}