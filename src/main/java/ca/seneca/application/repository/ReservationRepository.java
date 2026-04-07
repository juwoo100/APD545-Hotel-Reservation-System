package ca.seneca.application.repository;

import ca.seneca.application.db.Database;
import ca.seneca.application.model.Reservation;
import ca.seneca.application.model.enums.ReservationStatus;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ReservationRepository {
    private final Database db = Database.getInstance();

    public Reservation save(EntityManager em, Reservation r) {
        if (r.getReservationId() == null) {
            em.persist(r);
            return r;
        } else {
            return em.merge(r);
        }
    }

    public Optional<Reservation> findById(Integer id) {
        EntityManager em = db.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Reservation.class, id));
        } finally {
            em.close();
        }
    }

    public List<Reservation> findAll() {
        EntityManager em = db.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM Reservation r JOIN FETCH r.guest ORDER BY r.checkInDate DESC",
                    Reservation.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reservation> findByStatus(ReservationStatus status) {
        EntityManager em = db.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM Reservation r JOIN FETCH r.guest WHERE r.status=:s",
                    Reservation.class
            ).setParameter("s", status).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reservation> findByGuestName(String name) {
        EntityManager em = db.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r JOIN FETCH r.guest g " +
                                    "WHERE LOWER(g.firstName) LIKE :n OR LOWER(g.lastName) LIKE :n",
                            Reservation.class
                    ).setParameter("n", "%" + name.toLowerCase() + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reservation> findByDateRange(LocalDate from, LocalDate to) {
        EntityManager em = db.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r JOIN FETCH r.guest " +
                                    "WHERE r.checkInDate >= :from AND r.checkInDate <= :to",
                            Reservation.class
                    )
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}