package ca.seneca.application.repository;

import ca.seneca.application.model.Reservation;
import jakarta.persistence.EntityManager;

public class ReservationRepository {

    public Reservation save(EntityManager em, Reservation reservation) {
        if (reservation.getReservationId() == null) {
            em.persist(reservation);
            return reservation;
        }
        return em.merge(reservation);
    }

    public Reservation findById(EntityManager em, Integer id) {
        return em.find(Reservation.class, id);
    }
}